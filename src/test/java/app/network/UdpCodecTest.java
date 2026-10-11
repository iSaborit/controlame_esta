package app.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import app.network.protocol.MessageEnvelope;
import app.network.protocol.MessageType;
import app.network.protocol.Payload;
import app.network.protocol.UdpCodec;

public class UdpCodecTest {

    private UdpCodec codec;

    @Before
    public void setUp() {
        codec = new UdpCodec();
    }

    private MessageEnvelope sample() {
        return MessageEnvelope.usernameChange("uuid-1", 1, "alice", "alicia");
    }

    @Test
    public void roundtrip() {
        MessageEnvelope msgSent = sample();

        byte[] jason = codec.encode(msgSent)
                .orElseThrow(() -> new AssertionError("error to encode msg"));

        MessageEnvelope msgRecv = codec.decode(jason, jason.length)
                .orElseThrow(() -> new AssertionError("error when decoding msg"));

        assertEquals(msgRecv, msgSent);
    }

    @Test
    public void badCrcDropped() {
        MessageEnvelope msgSent = sample();

        byte[] jason = codec.encode(msgSent)
                .orElseThrow(() -> new AssertionError("error to encode msg"));

        jason[jason.length - 3] ^= 1;

        Optional<MessageEnvelope> msgRecv = codec.decode(jason, jason.length);

        assert (msgRecv.isEmpty());
    }

    @Test
    public void truncatedDropped() {
        MessageEnvelope msgSent = sample();

        byte[] fullMsg = codec.encode(msgSent)
                .orElseThrow(() -> new AssertionError("error to encode msg"));

        // Recortar los últimos 3 bytes usando Arrays.copyOf
        byte[] truncated = Arrays.copyOf(fullMsg, fullMsg.length - 3);

        Optional<MessageEnvelope> msgRecv = codec.decode(truncated, truncated.length);

        assert (msgRecv.isEmpty());
    }
}
