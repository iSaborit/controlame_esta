package app.network.handler;

import app.network.protocol.MessageEnvelope;
import java.net.InetSocketAddress;

public interface MessageHandler {
    void handle(MessageEnvelope msg, InetSocketAddress from);
}
