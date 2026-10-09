package app.network.handler;

import app.network.NetworkListener;
import app.network.NetworkMessage;
import java.net.InetSocketAddress;

public class UsernameChangedHandler implements MessageHandler {
    private final NetworkListener listener;

    public UsernameChangedHandler(NetworkListener listener) {
        this.listener = listener;
    }

    @Override
    public void handle(NetworkMessage msg, InetSocketAddress from) {
        // LAYOUT ONLY: listener.onPeerRenamed(senderId, newName)
    }
}
