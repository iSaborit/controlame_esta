package app.network.handler;

import app.model.ContactListModel;
import app.model.User;
import app.model.UserProfileModel;
import app.network.NetworkListener;
import app.network.NetworkService;
import app.network.protocol.MessageEnvelope;
import app.network.protocol.Payload;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class DiscoverHandler implements MessageHandler {
    private final NetworkService networkService;
    private UserProfileModel userProfileModel;
    private ContactListModel contactListModel;

    public DiscoverHandler(NetworkService networkService, UserProfileModel userProfileModel,
                           ContactListModel contactListModel) {
        this.networkService = networkService;
        this.userProfileModel = userProfileModel;
        this.contactListModel = contactListModel;
    }

    @Override
    public void handle(MessageEnvelope msg, InetSocketAddress from) {
        // answer PRESENCE_ACK, then listener.onPeerAnnounced(user)
        User me = userProfileModel.getCurrentUser();
        if (me == null || !userProfileModel.isConnected()) return;
        if (me.getId().equals(msg.from())) return; // ignore self-broadcast

        long now = System.currentTimeMillis();
        List<Payload.PeerInfo> peers = new ArrayList<>();

        // add myself to the list
        peers.add(new Payload.PeerInfo(me.getId(), me.getUsername(), now, me.getIpAddress(), me.getPort()));

        // add my contacts to the list
        for (User contact : List.copyOf(contactListModel.getContacts())) {
            if (contact.getId().equals(me.getId())) continue;
            peers.add(new Payload.PeerInfo(contact.getId(), contact.getUsername(), contact.getLastSeen(),
                    contact.getIpAddress(), contact.getPort()));
        }

        networkService.sendPresenceAck(me.getId(), me.getUsername(), from, msg.seq(), peers);
    }
}
