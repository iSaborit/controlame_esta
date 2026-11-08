package app.network.handler;

import app.model.User;
import app.model.UserProfileModel;
import app.network.NetworkListener;
import app.network.NetworkService;
import app.network.protocol.MessageEnvelope;
import app.network.protocol.Payload;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class PresenceAckHandler implements MessageHandler{
    private final NetworkService networkService;
    private final UserProfileModel userProfileModel;

    public PresenceAckHandler(NetworkService networkService, UserProfileModel userProfileModel) {
        this.networkService = networkService;
        this.userProfileModel = userProfileModel;
    }

    @Override
    public void handle(MessageEnvelope msg, InetSocketAddress from) {
        if (msg.payload() == null || msg.payload().peers() == null || msg.payload().peers().isEmpty() ||
                msg.inReplyTo() == null) {
            return;
        }

        User me = userProfileModel.getCurrentUser();
        if (me == null || !userProfileModel.isConnected() || me.getId().equals(msg.from())) {
            return;
        }

        List<User> users = new ArrayList<>();

        for (Payload.PeerInfo peer : msg.payload().peers()) {
            if (peer == null || peer.id() == null || peer.id().isEmpty() ||
                    peer.username() == null || peer.username().isEmpty() || peer.id().equals(me.getId())) {
                continue;
            }

            int port = 0;
            long lastSeen = 0;
            InetAddress ipAddress;

            if (peer.id().equals(msg.from())) {
                ipAddress = from.getAddress();
                port = from.getPort();
                lastSeen = System.currentTimeMillis();
            } else {
                if (peer.ipAddress() == null) continue;
                ipAddress = peer.ipAddress();
                port = peer.port();
                lastSeen = peer.lastSeen();
            }

            User user = new User(peer.id(), peer.username(), ipAddress);
            user.setPort(port);
            user.setLastSeen(lastSeen);

            users.add(user);
        }

        networkService.onPresenceAck(msg.inReplyTo(), users);
    }
}
