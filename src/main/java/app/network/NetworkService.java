package app.network;

import app.model.User;

import java.net.InetSocketAddress;

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
}
