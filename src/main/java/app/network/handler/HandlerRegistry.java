package app.network.handler;

import app.model.ContactListModel;
import app.model.UserProfileModel;
import app.network.NetworkService;
import app.network.protocol.MessageType;
import app.network.NetworkListener;
import java.util.EnumMap;
import java.util.Map;

public final class HandlerRegistry {
    private HandlerRegistry() {
    }

    public static Map<MessageType, MessageHandler> build(NetworkListener contactListener,
                                                         NetworkService networkService,
                                                         UserProfileModel userProfileModel,
                                                         ContactListModel contactListModel) {
        Map<MessageType, MessageHandler> handlers = new EnumMap<>(MessageType.class);
        handlers.put(MessageType.PRESENCE_ANNOUNCE, new PresenceAnnounceHandler(contactListener));
        handlers.put(MessageType.DISCOVER_CONTACTS_REQ, new DiscoverHandler(contactListener));
        handlers.put(MessageType.USERNAME_TAKEN, new UsernameTakenHandler(networkService));
        handlers.put(MessageType.USERNAME_CHANGED, new UsernameChangedHandler(contactListener));
        handlers.put(MessageType.DISCONNECT, new DisconnectHandler(contactListener));
        handlers.put(MessageType.CHECK_USERNAME_REQ,
                new CheckUsernameHandler(userProfileModel, contactListModel, networkService));
        return handlers;
    }
}
