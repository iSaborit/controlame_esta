package app.controller;

import app.model.ContactListModel;
import app.model.User;
import app.model.UserProfileModel;
import app.network.NetworkListener;
import app.network.NetworkService;
import app.view.ContactListView;

public class ContactController implements NetworkListener {
    private ContactListView contactView;
    private ContactListModel contactListModel;
    private UserProfileModel userModel;
    private NetworkService networkService;

    public ContactController(ContactListView contactView, ContactListModel contactListModel,
                             UserProfileModel userModel, NetworkService networkService) {
        this.contactView = contactView;
        this.contactListModel = contactListModel;
        this.userModel = userModel;
        this.networkService = networkService;
    }

    public void handleChangeUsername(String newUsername) {
        // LAYOUT ONLY: validate, then networkService.sendUsernameChange(...)
    }

    public void handleLogout() {
        // LAYOUT ONLY: networkService.sendDisconnect(...), clear model
    }

    @Override
    public void onPeerAnnounced(User user) {
        // LAYOUT ONLY: contactListModel.addContact(user)
    }

    @Override
    public void onPeerRenamed(String userId, String newName) {
        // LAYOUT ONLY: contactListModel.updateContactUsername(userId, newName)
    }

    @Override
    public void onPeerDisconnected(String userId) {
        // LAYOUT ONLY: contactListModel.removeContact(userId)
    }

    @Override
    public void onUsernameCheckResult(boolean available) {
        // LAYOUT ONLY: route for rename uniqueness check
    }
}
