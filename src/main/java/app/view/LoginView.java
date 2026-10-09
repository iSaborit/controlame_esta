package app.view;

import java.util.function.Consumer;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginView {
    private TextField usernameField;
    private Button submitButton;
    private Label errorLabel;
    private Consumer<String> onLoginSubmit;

    public LoginView() {
        this.usernameField = new TextField();
        this.submitButton = new Button();
        this.errorLabel = new Label();
    }

    public void setOnLoginSubmit(Consumer<String> handler) {
        // LAYOUT ONLY
    }

    public Parent getRoot() {
        return new VBox(10, usernameField, submitButton, errorLabel);
    }

    public String getUsernameInput() {
        return null;
    }

    public void showError(String message) {
        // LAYOUT ONLY
    }

    public void clearError() {
        // LAYOUT ONLY
    }

    public void hideView() {
        // LAYOUT ONLY
    }
}
