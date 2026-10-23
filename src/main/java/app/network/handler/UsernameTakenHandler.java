package app.network.handler;

import app.network.NetworkService;
import app.network.protocol.MessageEnvelope;

import java.net.InetSocketAddress;

public class UsernameTakenHandler implements MessageHandler {
    private final NetworkService networkService;

    public UsernameTakenHandler(NetworkService networkService) {
        this.networkService = networkService;
    }

    @Override
    public void handle(MessageEnvelope msg, InetSocketAddress from) {
        networkService.onUsernameTaken(msg.payload().username(), msg.inReplyTo());
    }
}
