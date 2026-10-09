package app.network.handler;

import app.network.NetworkListener;
import app.network.NetworkMessage;
import java.net.InetSocketAddress;

public class PresenceAnnounceHandler implements MessageHandler {
    private final NetworkListener listener;

    public PresenceAnnounceHandler(NetworkListener listener) {
        this.listener = listener;
    }

    @Override
    public void handle(NetworkMessage msg, InetSocketAddress from) {
        // LAYOUT ONLY: build User from msg + from, then listener.onPeerAnnounced(user)
    }
}
