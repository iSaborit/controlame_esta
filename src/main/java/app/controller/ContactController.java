package app.controller;

import app.model.ContactListModel;
import app.model.UserProfileModel;
import app.model.User;
import app.model.InvalidUsernameException;
import app.model.UsernameValidator;
import app.network.NetworkListener;
import app.network.NetworkService;
import app.view.ContactListView;

import java.util.Objects;

public class ContactController implements NetworkListener {
    private ContactListView contactView;
    private ContactListModel contactListModel;
    private UserProfileModel userModel;
    private NetworkService networkService;

    private String pendingUsernameToChange;

    public ContactController(ContactListView contactView, ContactListModel contactListModel,
                             UserProfileModel userModel, NetworkService networkService) {
        this.contactView = contactView;
        this.contactListModel = contactListModel;
        this.userModel = userModel;
        this.networkService = networkService;
    }

    public void handleChangeUsername(String newUsername) {
        // validate, then networkService.sendUsernameChange(...)
        User me = this.userModel.getCurrentUser();
        if (me == null || !this.userModel.isConnected()) return;

        String requested = newUsername == null ? "" : newUsername;
        if (requested.isEmpty() || requested.equals(me.getUsername())) return;

        if (!UsernameValidator.isValid(requested)) {
            this.contactView.showError("Invalid username.");
            return;
        }

        this.contactView.clearError();

        this.pendingUsernameToChange = requested;
        this.networkService.checkUsernameUniqueness(requested);
    }

    public void handleLogout() {
        // networkService.sendDisconnect(...), clear model
        User me = this.userModel.getCurrentUser();
        if (me != null && this.userModel.isConnected()) {
            this.networkService.sendDisconnect(me);
        }

        this.userModel.setConnected(false);

        this.contactView.clearError();
        this.contactListModel.clear();
        this.pendingUsernameToChange = null;
        this.userModel.setCurrentUser(null);
    }

    @Override
    public void onPeerAnnounced(User user) {
        if (user == null || !this.userModel.isConnected()) return;

        User me = this.userModel.getCurrentUser();
        if (me != null && Objects.equals(me.getId(), user.getId())) {
            return; // ignore broadcast if I am the sender
        }

        this.contactListModel.addContact(user);
    }

    @Override
    public void onPeerRenamed(String userId, String newName) {
        if (!this.userModel.isConnected()) return;
        this.contactListModel.updateContactUsername(userId, newName);
    }

    @Override
    public void onPeerDisconnected(String userId) {
        if (!this.userModel.isConnected()) return;
        this.contactListModel.removeContact(userId);
    }

    @Override
    public void onUsernameCheckResult(boolean available) {
        // route for rename uniqueness check
        if (this.pendingUsernameToChange == null) {
            return; // ignore if it's not our request
        }

        String requested = this.pendingUsernameToChange;
        this.pendingUsernameToChange = null;

        if (!available) {
            this.contactView.showError("Username not available.");
            return;
        }

        User me = this.userModel.getCurrentUser();
        if (me == null) return;

        String oldUsername = me.getUsername();
        try {
            me.setUsername(requested);
        } catch (InvalidUsernameException e) {
            this.contactView.showError("Could not set username. Please try again.");
            return;
        }

        this.networkService.sendUsernameChange(me, oldUsername, requested);
    }
}
