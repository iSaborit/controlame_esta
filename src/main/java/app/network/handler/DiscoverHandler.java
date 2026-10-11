package app.network.handler;

import app.network.NetworkListener;
import app.network.protocol.MessageEnvelope;
import java.net.InetSocketAddress;

public class DiscoverHandler implements MessageHandler {
    private final NetworkListener listener;

    public DiscoverHandler(NetworkListener listener) {
        this.listener = listener;
    }

    @Override
    public void handle(MessageEnvelope msg, InetSocketAddress from) {
        // LAYOUT ONLY: answer PRESENCE_ACK, then listener.onPeerAnnounced(user)
    }
}
