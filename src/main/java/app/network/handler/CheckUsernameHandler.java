package app.network.handler;

import app.model.ContactListModel;
import app.model.User;
import app.model.UserProfileModel;
import app.network.NetworkService;
import app.network.protocol.MessageEnvelope;
import java.net.InetSocketAddress;
import java.util.List;

public class CheckUsernameHandler implements MessageHandler {

    UserProfileModel userProfileModel;
    ContactListModel contactListModel;
    NetworkService networkService;

    public CheckUsernameHandler(
            UserProfileModel userProfileModel, ContactListModel contactListModel, NetworkService networkService
    ) {
        this.contactListModel = contactListModel;
        this.userProfileModel = userProfileModel;
        this.networkService = networkService;
    }

    @Override
    public void handle(MessageEnvelope msg, InetSocketAddress from) {
        String requested = msg.payload().username();
        User me = userProfileModel.getCurrentUser();
        if (me != null && requested.equalsIgnoreCase(me.getUsername())) {
            networkService.sendUsernameTaken(from, msg.seq(), requested, me.getUsername(), me.getId());
            return;
        }
        for (User contact : List.copyOf(contactListModel.getContacts())) {
            if (requested.equalsIgnoreCase(contact.getUsername())) {
                networkService.sendUsernameTaken(from, msg.seq(), requested, contact.getUsername(), contact.getId());
                return;
            }
        }
    }
}
