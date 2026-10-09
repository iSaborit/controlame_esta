package app.network;

import app.model.User;

public interface NetworkListener {
    void onPeerAnnounced(User user);

    void onPeerRenamed(String userId, String newName);

    void onPeerDisconnected(String userId);

    void onUsernameCheckResult(boolean available);
}
