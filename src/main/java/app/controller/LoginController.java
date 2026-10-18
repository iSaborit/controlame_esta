package app.controller;

import app.model.User;
import app.model.UserProfileModel;
import app.model.UsernameValidator;
import app.network.NetworkListener;
import app.network.NetworkService;
import app.view.LoginView;

import java.util.UUID;

public class LoginController implements NetworkListener {
    private LoginView loginView;
    private UserProfileModel userModel;
    private NetworkService networkService;
    private String pendingUsername;

    private Runnable onLoginSuccess;

    public LoginController(LoginView loginView, UserProfileModel userModel, NetworkService networkService) {
        this.loginView = loginView;
        this.userModel = userModel;
        this.networkService = networkService;
    }

    public void setOnLoginSuccess(Runnable r) {
        this.onLoginSuccess = r;
    }

    public void handleLoginSubmit(String requestedUsername) {
        // LAYOUT ONLY: validate locally, then
        // networkService.checkUsernameUniqueness(...)
        if (!UsernameValidator.isValid(requestedUsername)) {
            loginView.showError("Invalid username.");
            return;
        }

        this.pendingUsername = requestedUsername;
        loginView.clearError();
        loginView.setSubmitDisabled(true); // while waiting for answer

        networkService.checkUsernameUniqueness(requestedUsername);
    }

    public void onUsernameValidated(String username) {
        User me = new User(UUID.randomUUID().toString(), username, null); // UUID ici
        this.userModel.setCurrentUser(me);
        this.userModel.setConnected(true);

        this.networkService.startPresenceBroadcast(me);
        this.networkService.sendDiscoverRequest(me.getId());

        loginView.hideView(); // redondant?
        this.onLoginSuccess.run();
    }

    public void onUsernameRejected(String reason) {
        loginView.showError(reason);
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
        loginView.setSubmitDisabled(false); // re-activate button

        if (available) {
            this.onUsernameValidated(pendingUsername);
        } else {
            this.onUsernameRejected("Username is already in use.");
        }
    }
}
