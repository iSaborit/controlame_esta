package app.network;

import app.model.User;

public interface NetworkService {
    void checkUsernameUniqueness(String username);

    void startPresenceBroadcast(User localUser);

    void sendDiscoverRequest();

    void sendUsernameChange(String oldUsername, String newUsername);

    void sendDisconnect(User localUser);

    void startListening();

    void stopListening();

    void addNetworkListener(NetworkListener listener);

    void removeNetworkListener(NetworkListener listener);
}
