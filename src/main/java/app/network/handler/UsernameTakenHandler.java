package app.network.handler;

import app.network.NetworkListener;
import app.network.protocol.MessageEnvelope;

import java.net.InetSocketAddress;

public class UsernameTakenHandler implements MessageHandler {
    private final NetworkListener listener;

    public UsernameTakenHandler(NetworkListener listener) {
        this.listener = listener;
    }

    @Override
    public void handle(MessageEnvelope msg, InetSocketAddress from) {
        // LAYOUT ONLY: listener.onUsernameCheckResult(false)
    }
}
