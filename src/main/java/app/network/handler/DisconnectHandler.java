package app.network.handler;

import app.network.NetworkListener;
import app.network.NetworkMessage;
import java.net.InetSocketAddress;

public class DisconnectHandler implements MessageHandler {
    private final NetworkListener listener;

    public DisconnectHandler(NetworkListener listener) {
        this.listener = listener;
    }

    @Override
    public void handle(NetworkMessage msg, InetSocketAddress from) {
        // LAYOUT ONLY: listener.onPeerDisconnected(senderId)
    }
}
