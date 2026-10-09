package app.model;

public interface ContactListObserver {
    void onContactAdded(User user);

    void onContactUpdated(User user);

    void onContactRemoved(User user);
}
