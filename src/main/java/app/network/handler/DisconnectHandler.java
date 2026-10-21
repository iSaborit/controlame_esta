package app.network.handler;

import app.network.NetworkListener;
import app.network.protocol.MessageEnvelope;
import java.net.InetSocketAddress;

public class DisconnectHandler implements MessageHandler {
    private final NetworkListener listener;

    public DisconnectHandler(NetworkListener listener) {
        this.listener = listener;
    }

    @Override
    public void handle(MessageEnvelope msg, InetSocketAddress from) {
        // listener.onPeerDisconnected(senderId)
        this.listener.onPeerDisconnected(msg.from());
    }
}
