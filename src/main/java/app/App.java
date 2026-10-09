package app;

import app.controller.ContactController;
import app.controller.LoginController;
import app.model.ContactListModel;
import app.model.UserProfileModel;
import app.network.MessageType;
import app.network.NetworkManager;
import app.network.handler.HandlerRegistry;
import app.network.handler.MessageHandler;
import app.view.ContactListView;
import app.view.LoginView;
import java.util.Map;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        AppConfig config = AppConfig.defaults();
        UserProfileModel userModel = new UserProfileModel();
        ContactListModel contactListModel = new ContactListModel();
        NetworkManager networkManager = new NetworkManager(config);

        LoginView loginView = new LoginView();
        ContactListView contactView = new ContactListView();
        contactListModel.addObserver(contactView);

        LoginController loginController = new LoginController(loginView, userModel, networkManager);
        ContactController contactController = new ContactController(contactView, contactListModel, userModel, networkManager);

        // Flow: network -> controller -> model -> view (no direct network -> model)
        networkManager.addNetworkListener(loginController);
        networkManager.addNetworkListener(contactController);
        Map<MessageType, MessageHandler> handlers = HandlerRegistry.build(loginController, contactController);
        networkManager.registerAllHandlers(handlers);

        loginView.setOnLoginSubmit(loginController::handleLoginSubmit);
        contactView.setOnChangeUsername(contactController::handleChangeUsername);
        contactView.setOnLogout(contactController::handleLogout);

        primaryStage.setScene(new Scene(loginView.getRoot(), 300, 150));
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
