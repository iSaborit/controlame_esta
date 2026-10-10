package app.network;

import app.AppConfig;
import app.model.User;
import app.network.handler.MessageHandler;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class NetworkManager implements NetworkService {
    private DatagramSocket udpSocket;
    private final AppConfig config;
    private int port;
    private boolean listening;
    private final Map<MessageType, MessageHandler> handlers = new EnumMap<>(MessageType.class);
    private final List<NetworkListener> listeners = new ArrayList<>();

    public NetworkManager(AppConfig config) {
        this.config = config;
        this.port = config.getPort();
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

    /**
     * Async check per sequence diagram: broadcasts CHECK_USERNAME_REQ,
     * waits ~1.5s; result arrives via NetworkListener.onUsernameCheckResult.
     */
    @Override
    public void checkUsernameUniqueness(String username) {
        // LAYOUT ONLY
    }

    @Override
    public void startPresenceBroadcast(User localUser) {
        NetworkMessage msg = new NetworkMessage(
                MessageType.PRESENCE_ANNOUNCE,
                localUser.getId(),
                localUser.getUsername());
        sendBroadcast(msg);
    }

    @Override
    public void sendDiscoverRequest(String localUserId) {
        NetworkMessage msg = new NetworkMessage(
                MessageType.DISCOVER_CONTACTS_REQ,
                localUserId,
                ""
        );
        sendBroadcast(msg);
    }

    @Override
    public void sendUsernameChange(String localUserId, String oldUsername, String newUsername) {
        // Format du message : "ancienPseudo:nouveauPseudo"
        String payload = oldUsername + ":" + newUsername;

        NetworkMessage msg = new NetworkMessage(
                MessageType.USERNAME_CHANGED,
                localUserId,
                payload
        );
        sendBroadcast(msg);
    }

    @Override
    public void sendDisconnect(User localUser) {
        if (localUser == null) return;

        NetworkMessage msg = new NetworkMessage(
                MessageType.DISCONNECT,
                localUser.getId(),
                localUser.getUsername()
        );
        sendBroadcast(msg);
    }

    @Override
    public void startListening() {
        if (listening) return;

        try {
            // On ouvre le socket UDP sur le port configuré
            this.udpSocket = new DatagramSocket(this.port);
            this.udpSocket.setBroadcast(true); // Autorise l'envoi/réception en broadcast
            this.listening = true;

            // thread pour ne pas bloquer l'application pendant la réception
            Thread receiveThread = new Thread(() -> {
                while (listening) {
                    try {
                        byte[] buf = new byte[1024]; // buffer pour la réception
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

    private void sendPacket(NetworkMessage message, InetAddress destinationAddress, int destinationPort) {
        try {
            // On sérialise notre objet NetworkMessage en tableau d'octets
            byte[] bytesToSend = message.serialize();
            if (bytesToSend == null) return;

            // On prépare le paquet UDP
            DatagramPacket outPacket = new DatagramPacket(
                    bytesToSend,
                    bytesToSend.length,
                    destinationAddress,
                    destinationPort
            );

            // On l'envoie sur le socket UDP existant
            if (udpSocket != null && !udpSocket.isClosed()) {
                udpSocket.send(outPacket);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'envoi du message : " + e.getMessage());
        }
    }

    private void sendBroadcast(NetworkMessage message) {
        try {
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            sendPacket(message, broadcastAddress, this.port);
        } catch (UnknownHostException e) {
            System.err.println("Erreur lors du broadcast du message : " + e);
        }
    }

    private void processIncomingPacket(DatagramPacket packet) {
        NetworkMessage msg = NetworkMessage.deserialize(packet.getData());
        MessageHandler handler = handlers.get(msg.getType());
        if (handler != null) {
            handler.handle(msg, (InetSocketAddress) packet.getSocketAddress());
        }
    }
}
