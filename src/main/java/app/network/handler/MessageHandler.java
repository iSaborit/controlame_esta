package app.network.handler;

import app.network.NetworkMessage;
import java.net.InetSocketAddress;

public interface MessageHandler {
    void handle(NetworkMessage msg, InetSocketAddress from);
}
