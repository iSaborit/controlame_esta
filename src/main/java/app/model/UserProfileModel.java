package app.model;

public class UserProfileModel {
    private User currentUser;
    private boolean connected;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        // LAYOUT ONLY
        this.currentUser = user;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void setConnected(boolean connected) {
        // LAYOUT ONLY
        this.connected = connected;
    }
}
