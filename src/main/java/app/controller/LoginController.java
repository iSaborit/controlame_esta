package app.controller;

import app.model.User;
import app.model.UserProfileModel;
import app.network.NetworkListener;
import app.network.NetworkService;
import app.view.LoginView;

public class LoginController implements NetworkListener {
    private LoginView loginView;
    private UserProfileModel userModel;
    private NetworkService networkService;

    public LoginController(LoginView loginView, UserProfileModel userModel, NetworkService networkService) {
        this.loginView = loginView;
        this.userModel = userModel;
        this.networkService = networkService;
    }

    public void handleLoginSubmit(String requestedUsername) {
        // LAYOUT ONLY: validate locally, then networkService.checkUsernameUniqueness(...)
    }

    public void onUsernameValidated(String username) {
        // LAYOUT ONLY
    }

    public void onUsernameRejected(String reason) {
        // LAYOUT ONLY
    }

    @Override
    public void onPeerAnnounced(User user) {
        // LAYOUT ONLY: not used during login
    }

    @Override
    public void onPeerRenamed(String userId, String newName) {
        // LAYOUT ONLY: not used during login
    }

    @Override
    public void onPeerDisconnected(String userId) {
        // LAYOUT ONLY: not used during login
    }

    @Override
    public void onUsernameCheckResult(boolean available) {
        // LAYOUT ONLY: route to onUsernameValidated / onUsernameRejected
    }
}
