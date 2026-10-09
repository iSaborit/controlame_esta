package app.model;

import java.util.ArrayList;
import java.util.Objects;

public class ContactListModel {
    private java.util.List<User> activeContacts;
    private java.util.List<ContactListObserver> observers;

    public ContactListModel() {
        this.activeContacts = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public void addContact(User user) {
        // LAYOUT ONLY
        this.activeContacts.add(user);
    }

    public void removeContact(String userId) {
        // LAYOUT ONLY
        this.activeContacts.removeIf(element -> Objects.equals(element.getId(), userId));
    }

    public void updateContactUsername(String userId, String newUsername) {
        // LAYOUT ONLY
        for (User element : this.activeContacts) {
            if (Objects.equals(element.getId(), userId)) {
                try {
                    element.setUsername(newUsername);
                } catch (Exception e) {
                    System.err.println("[CONTACT LIST MODEL] Should not crash, but... " + e);
                }
            }
        }
    }

    public java.util.List<User> getContacts() {
        return this.activeContacts;
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
