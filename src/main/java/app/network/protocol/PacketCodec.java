package app.network.protocol;

import java.util.Optional;

public interface PacketCodec {
    Optional<byte[]> encode(MessageEnvelope env);

    Optional<MessageEnvelope> decode(byte[] data, int length);
}
