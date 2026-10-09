package app.model;

import java.util.ArrayList;

public class ContactListModel {
    private java.util.List<User> activeContacts;
    private java.util.List<ContactListObserver> observers;

    public ContactListModel() {
        this.activeContacts = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public void addContact(User user) {
        // LAYOUT ONLY
    }

    public void removeContact(String userId) {
        // LAYOUT ONLY
    }

    public void updateContactUsername(String userId, String newUsername) {
        // LAYOUT ONLY
    }

    public java.util.List<User> getContacts() {
        return null;
    }

    public void addObserver(ContactListObserver observer) {
        // LAYOUT ONLY
    }

    public void removeObserver(ContactListObserver observer) {
        // LAYOUT ONLY
    }

    private void notifyContactAdded(User user) {
        // LAYOUT ONLY
    }

    private void notifyContactUpdated(User user) {
        // LAYOUT ONLY
    }

    private void notifyContactRemoved(User user) {
        // LAYOUT ONLY
    }
}
