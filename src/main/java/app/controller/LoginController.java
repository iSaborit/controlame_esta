package app.controller;

import app.model.InvalidUsernameException;
import app.model.User;
import app.model.UserProfileModel;
import app.model.UsernameValidator;
import app.network.NetworkListener;
import app.network.NetworkService;
import app.view.LoginView;

import java.util.Objects;

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
        // LAYOUT ONLY: validate locally, then
        // networkService.checkUsernameUniqueness(...)

        UsernameValidator.isValid(requestedUsername);

        networkService.checkUsernameUniqueness(requestedUsername);
    }

    public void onUsernameValidated(String username) throws InvalidUsernameException {
        // this function can throw... maybe checking here first?
        this.userModel.getCurrentUser().setUsername(username);

        // + broadcast:
        this.networkService.startPresenceBroadcast(this.userModel.getCurrentUser());
        // + contact discovery request
        this.networkService.sendDiscoverRequest();
    }

    public void onUsernameRejected(String reason) {
        // LAYOUT ONLY
        System.err.println("[ERROR] [LoginController] Username rejected: " + reason);
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
