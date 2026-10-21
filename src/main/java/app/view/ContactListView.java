package app.view;

import app.model.ContactListObserver;
import app.model.User;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ContactListView implements ContactListObserver {
    private ListView<User> contactListView;
    private ObservableList<User> observableContacts;
    private final Button changeUsernameButton;
    private final Button logoutButton;
    private Consumer<String> onChangeUsername;
    private Runnable onLogout;

    private Label errorLabel;
    private final Parent rootNode;

    public ContactListView() {
        this.observableContacts = FXCollections.observableArrayList();
        this.contactListView = new ListView<>(observableContacts);
        this.changeUsernameButton = new Button("Change Username");
        this.logoutButton = new Button("Logout");
        this.errorLabel = new Label();
        this.rootNode = new VBox(10, contactListView, changeUsernameButton, logoutButton, errorLabel);

        contactListView.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty || user == null ? null : user.getUsername());
            }
        });
    }

    public Parent getRoot() {
        return this.rootNode;
    }

    public void setOnChangeUsername(Consumer<String> handler) {
        this.onChangeUsername = handler;
        this.changeUsernameButton.setOnAction(event -> {
            if (this.onChangeUsername == null) { return; }
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Username");
            dialog.setHeaderText("Enter new username:");
            Optional<String> text = dialog.showAndWait();
            text.ifPresent(username -> this.onChangeUsername.accept(username));
        });
    }

    public void setOnLogout(Runnable handler) {
        this.onLogout = handler;
        this.logoutButton.setOnAction(event -> {
            if (this.onLogout == null) { return; }
            this.onLogout.run();
        });
    }

    public void displayContacts(List<User> contacts) {
        if (contacts == null) return;
        Platform.runLater(() -> this.observableContacts.setAll(contacts));
    }

    public void updateView() {
        Platform.runLater(() -> this.contactListView.refresh());
    }

    public void showView() {
        Platform.runLater(() -> this.rootNode.setVisible(true));
    }

    @Override
    public void onContactAdded(User user) {
        Platform.runLater(() -> {
            boolean exists = this.observableContacts.stream().anyMatch(
                    contact -> Objects.equals(contact.getId(), user.getId())
            );
            if (!exists) {
                this.observableContacts.add(user);
            }
        });
    }

    @Override
    public void onContactUpdated(User user) {
        Platform.runLater(() -> {
            for (int i = 0; i < this.observableContacts.size(); i++) {
                if (Objects.equals(this.observableContacts.get(i).getId(), user.getId())) {
                    this.observableContacts.set(i, user); // replace user
                    return;
                }
            }
            this.observableContacts.add(user); // if user not found, add it
        });
    }

    @Override
    public void onContactRemoved(User user) {
        Platform.runLater(() -> {
            this.observableContacts.removeIf(contact -> Objects.equals(contact.getId(), user.getId()));
        });
    }

    public void showError(String message) {
        this.errorLabel.setText(message);
    }
    public void clearError() {
        this.errorLabel.setText("");
    }
}
