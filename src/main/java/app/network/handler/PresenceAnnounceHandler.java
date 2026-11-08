package app.network.handler;

import app.model.User;
import app.network.NetworkListener;
import app.network.protocol.MessageEnvelope;
import java.net.InetSocketAddress;

public class PresenceAnnounceHandler implements MessageHandler {
    private final NetworkListener listener;

    public PresenceAnnounceHandler(NetworkListener listener) {
        this.listener = listener;
    }

    @Override
    public void handle(MessageEnvelope msg, InetSocketAddress from) {
        // build User from msg + from, then listener.onPeerAnnounced(user)
        if (msg.payload() == null || msg.payload().username() == null) return;

        User user = new User(msg.from(), msg.payload().username(), from.getAddress());
        user.setPort(from.getPort());
        user.setLastSeen(System.currentTimeMillis());

        this.listener.onPeerAnnounced(user);
    }
}
