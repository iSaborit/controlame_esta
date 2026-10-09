package app.network.handler;

import app.network.MessageType;
import app.network.NetworkListener;
import java.util.EnumMap;
import java.util.Map;

public final class HandlerRegistry {
    private HandlerRegistry() {
    }

    public static Map<MessageType, MessageHandler> build(NetworkListener loginListener,
                                                         NetworkListener contactListener) {
        Map<MessageType, MessageHandler> handlers = new EnumMap<>(MessageType.class);
        handlers.put(MessageType.PRESENCE_ANNOUNCE, new PresenceAnnounceHandler(contactListener));
        handlers.put(MessageType.DISCOVER_CONTACTS_REQ, new DiscoverHandler(contactListener));
        handlers.put(MessageType.USERNAME_TAKEN, new UsernameTakenHandler(loginListener));
        handlers.put(MessageType.USERNAME_CHANGED, new UsernameChangedHandler(contactListener));
        handlers.put(MessageType.DISCONNECT, new DisconnectHandler(contactListener));
        return handlers;
    }
}
