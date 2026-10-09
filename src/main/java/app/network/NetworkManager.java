package app.network;

import app.AppConfig;
import app.model.User;
import app.network.handler.MessageHandler;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
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
        // LAYOUT ONLY
    }

    public void registerAllHandlers(Map<MessageType, MessageHandler> handlers) {
        // LAYOUT ONLY
    }

    @Override
    public void addNetworkListener(NetworkListener listener) {
        // LAYOUT ONLY
    }

    @Override
    public void removeNetworkListener(NetworkListener listener) {
        // LAYOUT ONLY
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
        // LAYOUT ONLY
    }

    @Override
    public void sendDiscoverRequest() {
        // LAYOUT ONLY
    }

    @Override
    public void sendUsernameChange(String oldUsername, String newUsername) {
        // LAYOUT ONLY
    }

    @Override
    public void sendDisconnect(User localUser) {
        // LAYOUT ONLY
    }

    @Override
    public void startListening() {
        // LAYOUT ONLY
    }

    @Override
    public void stopListening() {
        // LAYOUT ONLY
    }

    private void processIncomingPacket(DatagramPacket packet) {
        NetworkMessage msg = NetworkMessage.deserialize(packet.getData());
        MessageHandler handler = handlers.get(msg.getType());
        if (handler != null) {
            handler.handle(msg, (InetSocketAddress) packet.getSocketAddress());
        }
    }
}
