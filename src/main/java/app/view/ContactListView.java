package app.view;

import app.model.ContactListObserver;
import app.model.User;
import java.util.List;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class ContactListView implements ContactListObserver {
    private ListView<String> contactListView;
    private ObservableList<String> observableContacts;
    private Button changeUsernameButton;
    private Button logoutButton;
    private Consumer<String> onChangeUsername;
    private Runnable onLogout;

    public ContactListView() {
        this.observableContacts = FXCollections.observableArrayList();
        this.contactListView = new ListView<>(observableContacts);
        this.changeUsernameButton = new Button();
        this.logoutButton = new Button();
    }

    public void setOnChangeUsername(Consumer<String> handler) {
        // LAYOUT ONLY
    }

    public void setOnLogout(Runnable handler) {
        // LAYOUT ONLY
    }

    public void displayContacts(List<User> contacts) {
        // LAYOUT ONLY
    }

    public void updateView() {
        // LAYOUT ONLY
    }

    public void showView() {
        // LAYOUT ONLY
    }

    @Override
    public void onContactAdded(User user) {
        // LAYOUT ONLY
    }

    @Override
    public void onContactUpdated(User user) {
        // LAYOUT ONLY
    }

    @Override
    public void onContactRemoved(User user) {
        // LAYOUT ONLY
    }
}
