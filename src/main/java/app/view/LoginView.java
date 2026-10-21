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

    private final Parent rootNode;

    public LoginView() {
        this.usernameField = new TextField();
        this.submitButton = new Button("Login");
        this.errorLabel = new Label();
        this.rootNode = new VBox(10, usernameField, submitButton, errorLabel);
    }

    public void setOnLoginSubmit(Consumer<String> handler) {
        this.onLoginSubmit = handler;
        this.submitButton.setOnAction(event -> {
            if (this.onLoginSubmit == null) return;
            this.onLoginSubmit.accept(getUsernameInput());
        });
    }

    public void setSubmitDisabled(boolean disabled) {
        submitButton.setDisable(disabled);
    }

    public Parent getRoot() {
        return rootNode;
    }

    public String getUsernameInput() {
        return usernameField.getText();
    }

    public void showError(String message) {
        errorLabel.setText(message);
    }

    public void clearError() {
        errorLabel.setText("");
    }

    public void hideView() {
        rootNode.setVisible(false);
    }
}
