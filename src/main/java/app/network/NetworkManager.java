package app.network;

import app.AppConfig;
import app.model.User;
import app.network.handler.MessageHandler;
import app.network.protocol.*;
import javafx.application.Platform;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class NetworkManager implements NetworkService {
    private DatagramSocket udpSocket;
    private final AppConfig config;
    private int port;
    private boolean listening;
    private final Map<MessageType, MessageHandler> handlers = new EnumMap<>(MessageType.class);
    private final List<NetworkListener> listeners = new ArrayList<>();

    private final PacketCodec codec;
    private final AtomicLong seq;

    record Pending(String username, long seq, List<ScheduledFuture<?>> todo) {
        void cancel() { todo.forEach(t -> t.cancel(false)); }
    }

    private final AtomicReference<Pending> pendingUsernameCheck = new AtomicReference<>();

    private final ScheduledExecutorService requestScheduler =
            Executors.newSingleThreadScheduledExecutor();

    private static final long NO_PENDING_DISCOVER = -1L;
    private final AtomicLong pendingDiscoverSeq = new AtomicLong(NO_PENDING_DISCOVER);

    public NetworkManager(AppConfig config) {
        this.config = config;
        this.port = config.getPort();
        this.codec = new UdpCodec();
        this.seq = new AtomicLong();
    }

    public NetworkManager(AppConfig config, PacketCodec codec) {
        this.config = config;
        this.port = config.getPort();
        this.codec = codec;
        this.seq = new AtomicLong();
    }

    public void registerHandler(MessageType type, MessageHandler handler) {
        this.handlers.put(type, handler);
    }

    public void registerAllHandlers(Map<MessageType, MessageHandler> handlers) {
        this.handlers.putAll(handlers);
    }

    @Override
    public void addNetworkListener(NetworkListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeNetworkListener(NetworkListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void onUsernameTaken(String username, Long inReplyTo) {
        Pending pending = this.pendingUsernameCheck.get();
        if (pending == null) return;
        if (inReplyTo == null || inReplyTo != pending.seq()) return; // not our request
        if (username == null || !username.equalsIgnoreCase(pending.username())) return;
        if (!this.pendingUsernameCheck.compareAndSet(pending, null)) return; // already resolved

        pending.cancel(); // ← cancel timeout (and re-send)
        for (NetworkListener listener : listeners) {
            Platform.runLater(() -> listener.onUsernameCheckResult(false));
        }
    }

    @Override
    public void sendUsernameTaken(InetSocketAddress to, long forSeq, String requestedUsername,
                                  String by, String responderId) {
        MessageEnvelope reply = MessageEnvelope.taken(
                responderId, this.seq.incrementAndGet(), forSeq, requestedUsername, by
        );

        sendPacket(reply, to.getAddress(), to.getPort());
    }

    @Override
    public void sendPresenceAck(String responderId, String username, InetSocketAddress to, long forSeq, List<Payload.PeerInfo> peers) {
        if (to == null || peers == null || peers.isEmpty()) return;

        MessageEnvelope reply = MessageEnvelope.ack(responderId, this.seq.incrementAndGet(), forSeq, username, peers);
        sendPacket(reply, to.getAddress(), to.getPort());
    }

    @Override
    public void onPresenceAck(Long inReplyTo, List<User> users) {
        if (inReplyTo == null || users == null || users.isEmpty()) {
            return;
        }

        long pending = this.pendingDiscoverSeq.get();
        if (pending == NO_PENDING_DISCOVER || inReplyTo != pending) {
            return;
        }

        for (User user : users) {
            if (user == null) continue;

            for (NetworkListener listener : listeners) {
                Platform.runLater(() -> listener.onPeerAnnounced(user));
            }
        }
    }

    private void expireUsernameCheck() {
        Pending pending = this.pendingUsernameCheck.get();
        if (pending == null || !this.pendingUsernameCheck.compareAndSet(pending, null)) {
            return;  // already resolved by a USERNAME_TAKEN
        }
        for (NetworkListener listener : listeners) {
            Platform.runLater(() -> listener.onUsernameCheckResult(true));
        }
    }

    /**
     * Async check per sequence diagram: broadcasts CHECK_USERNAME_REQ,
     * waits ~1.5s; result arrives via NetworkListener.onUsernameCheckResult.
     */
    @Override
    public void checkUsernameUniqueness(String username) {
        long seqNb = this.seq.incrementAndGet();
        String requesterId = UUID.randomUUID().toString();

        List<ScheduledFuture<?>> todo = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            int delay = i * 400;
            todo.add(this.requestScheduler.schedule(() -> {
                        MessageEnvelope request = MessageEnvelope.checkRequest(requesterId, seqNb, username);
                        sendBroadcast(request);
                    },
                    delay, TimeUnit.MILLISECONDS
            ));
        }

        todo.add(this.requestScheduler.schedule(
                () -> this.expireUsernameCheck(),
                config.getUsernameCheckTimeoutMs(), TimeUnit.MILLISECONDS
        ));

        // only one check at a time
        Pending previous = this.pendingUsernameCheck.getAndSet(new Pending(username, seqNb, todo));
        if (previous != null) previous.cancel();
    }

    @Override
    public void startPresenceBroadcast(User localUser) {
        MessageEnvelope msg = MessageEnvelope.announce(localUser.getId(), seq.incrementAndGet(),
                localUser.getUsername());
        sendBroadcast(msg);
    }

    @Override
    public void sendDiscoverRequest(String localUserId) {
        if (localUserId == null) return;

        long seqNb = this.seq.incrementAndGet();
        this.pendingDiscoverSeq.set(seqNb);

        MessageEnvelope request = MessageEnvelope.discover(localUserId, seqNb);

        // Send the request 3 times, same method as for check username request:
        for (int i = 0; i < 3; i++) {
            int delay = i * 400;
            this.requestScheduler.schedule(() -> {
                if (this.pendingDiscoverSeq.get() != seqNb) return;
                sendBroadcast(request);
            }, delay, TimeUnit.MILLISECONDS);
        }

        this.requestScheduler.schedule(
                () -> this.pendingDiscoverSeq.compareAndSet(seqNb, NO_PENDING_DISCOVER),
                config.getDiscoveryTimeoutMs(),
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public void sendUsernameChange(User user, String oldUsername, String newUsername) {
        MessageEnvelope msg = MessageEnvelope.usernameChange(user.getId(), seq.incrementAndGet(), oldUsername,
                newUsername);
        sendBroadcast(msg);
    }

    @Override
    public void sendDisconnect(User user) {
        if (user == null)
            return;

        MessageEnvelope msg = MessageEnvelope.bye(user.getId(), seq.incrementAndGet(), user.getUsername());
        sendBroadcast(msg);
    }

    @Override
    public void startListening() {
        if (listening)
            return;

        try {
            // On ouvre le socket UDP sur le port configuré
            this.udpSocket = new DatagramSocket(this.port);
            this.udpSocket.setBroadcast(true); // Autorise l'envoi/réception en broadcast
            this.listening = true;

            // thread pour ne pas bloquer l'application pendant la réception
            Thread receiveThread = new Thread(() -> {
                while (listening) {
                    try {
                        byte[] buf = new byte[8192]; // JSON envelopes exceed the old 1024B layout
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        udpSocket.receive(packet); // Opération bloquante

                        // On traite le paquet reçu
                        processIncomingPacket(packet);

                    } catch (IOException e) {
                        if (!listening) {
                            // Fermeture normale du socket via stopListening()
                            break;
                        }
                        System.err.println("Erreur de réception : " + e.getMessage());
                    }
                }
            });

            receiveThread.setDaemon(true); // Se ferme automatiquement quand l'application quitte
            receiveThread.start();

        } catch (SocketException e) {
            System.err.println("Impossible de démarrer le serveur UDP sur le port " + this.port);
        }
    }

    @Override
    public void stopListening() {
        this.listening = false;
        if (this.udpSocket != null && !this.udpSocket.isClosed()) {
            this.udpSocket.close(); // Débloque le socket.receive() dans le thread
        }
    }

    private void sendPacket(MessageEnvelope message, InetAddress destinationAddress, int destinationPort) {
        Optional<byte[]> bytesToSend = codec.encode(message);
        if (bytesToSend.isEmpty()) return;

        try {
            // On prépare le paquet UDP
            DatagramPacket outPacket = new DatagramPacket(
                    bytesToSend.get(),
                    bytesToSend.get().length,
                    destinationAddress,
                    destinationPort);

            // On l'envoie sur le socket UDP existant
            if (udpSocket != null && !udpSocket.isClosed()) {
                udpSocket.send(outPacket);
            }
        } catch (IOException e) {
            System.err.println("error sending message : " + e.getMessage());
        }
    }

    private Set<InetAddress> getBroadcastAddress() {
        Set<InetAddress> broadcastAddresses = new LinkedHashSet<>();

        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback() || ni.isPointToPoint()) continue;

                for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                    InetAddress broadcast = ia.getBroadcast();
                    if (ia.getAddress() instanceof Inet4Address && broadcast != null) {
                        broadcastAddresses.add(broadcast);
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("SocketException : " + e);
        }

        return broadcastAddresses;
    }

    private void sendBroadcast(MessageEnvelope message) {
        Set<InetAddress> broadcastAddresses = getBroadcastAddress();
        if (broadcastAddresses.isEmpty()) {
            InetAddress loopback = InetAddress.getLoopbackAddress();
            System.err.println(
                    "[NetworkManager] no broadcast address found, falling back to " + loopback.getHostAddress());
            sendPacket(message, loopback, this.port);
            return;
        }

        for (InetAddress address : broadcastAddresses) {
            sendPacket(message, address, this.port);
        }
    }

    private void processIncomingPacket(DatagramPacket packet) {
        Optional<MessageEnvelope> decoded = codec.decode(packet.getData(), packet.getLength());
        if (decoded.isEmpty()) return;

        MessageEnvelope msg = decoded.get();
        MessageHandler handler = handlers.get(msg.type());
        if (handler == null) return;
        try {
            handler.handle(msg, (InetSocketAddress) packet.getSocketAddress());
        } catch (Exception e) {
            System.err.println("[NetworkManager] handler failed for " + msg.type() + ": " + e);
        }
    }
}
