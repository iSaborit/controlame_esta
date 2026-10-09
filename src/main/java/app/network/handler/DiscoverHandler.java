package app.network.handler;

import app.network.NetworkListener;
import app.network.NetworkMessage;
import java.net.InetSocketAddress;

public class DiscoverHandler implements MessageHandler {
    private final NetworkListener listener;

    public DiscoverHandler(NetworkListener listener) {
        this.listener = listener;
    }

    @Override
    public void handle(NetworkMessage msg, InetSocketAddress from) {
        // LAYOUT ONLY: answer PRESENCE_ACK, then listener.onPeerAnnounced(user)
    }
}
