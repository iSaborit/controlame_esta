package app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactListModel {
    private java.util.List<User> activeContacts;
    private java.util.List<ContactListObserver> observers;

    public ContactListModel() {
        this.activeContacts = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public void addContact(User user) {
        if (user == null) return;

        for (User contact : this.activeContacts) {
            if (Objects.equals(contact.getId(), user.getId())) {
                return; // user already known
            }
        }

        this.activeContacts.add(user);
        notifyContactAdded(user);
    }

    public void removeContact(String userId) {
        if (userId == null) return;

        User removed = null;
        for (User contact : this.activeContacts) {
            if (Objects.equals(contact.getId(), userId)) {
                removed = contact;
                break;
            }
        }
        if (removed == null) return;

        this.activeContacts.remove(removed);
        notifyContactRemoved(removed);
    }

    public void updateContactUsername(String userId, String newUsername) {
        for (User element : this.activeContacts) {
            if (Objects.equals(element.getId(), userId)) {
                try {
                    element.setUsername(newUsername);
                } catch (Exception e) {
                    System.err.println("[CONTACT LIST MODEL] Should not crash, but... " + e);
                    return;
                }
                notifyContactUpdated(element);
                return;
            }
        }
    }

    public void clear() {
        List<User> tmp = List.copyOf(this.activeContacts);
        this.activeContacts.clear();
        for (User contact : tmp) {
            notifyContactRemoved(contact);
        }
    }

    public java.util.List<User> getContacts() {
        return this.activeContacts;
    }

    public void addObserver(ContactListObserver observer) {
        if (observer != null && !this.observers.contains(observer)) {
            this.observers.add(observer);
        }
    }

    public void removeObserver(ContactListObserver observer) {
        this.observers.remove(observer);
    }

    private void notifyContactAdded(User user) {
        for (ContactListObserver observer : this.observers) {
            observer.onContactAdded(user);
        }
    }

    private void notifyContactUpdated(User user) {
        for (ContactListObserver observer : this.observers) {
            observer.onContactUpdated(user);
        }
    }

    private void notifyContactRemoved(User user) {
        for (ContactListObserver observer : this.observers) {
            observer.onContactRemoved(user);
        }
    }
}
