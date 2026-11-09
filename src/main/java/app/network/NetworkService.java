package app.network;

import app.model.User;
import app.network.protocol.Payload;

import java.net.InetSocketAddress;
import java.util.List;

public interface NetworkService {
    void checkUsernameUniqueness(String username);

    void startPresenceBroadcast(User localUser);

    void sendDiscoverRequest(String localUserId);

    void sendUsernameChange(User user, String oldUsername, String newUsername);

    void sendDisconnect(User localUser);

    void startListening();

    void stopListening();

    void addNetworkListener(NetworkListener listener);

    void removeNetworkListener(NetworkListener listener);

    void onUsernameTaken(String username, Long inReplyTo);

    void sendUsernameTaken(InetSocketAddress to, long forSeq, String requestedUsername, String by, String responderId);

    void sendPresenceAck(String responderId, String username, InetSocketAddress to, long forSeq, List<Payload.PeerInfo> peers);

    void onPresenceAck(Long inReplyTo, List<User> users);
}
