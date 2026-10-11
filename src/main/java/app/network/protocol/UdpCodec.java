// Rustacean filosophical decitions: 
// Instead of throwing runtime exceptions, 
// we return an optional (it adds overhead, yes, but negligable
// when we consider network + jsonification)
//
// We avoid NullPointerException. 

package app.network.protocol;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.zip.CRC32;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

public final class UdpCodec implements PacketCodec {
    static final short MAGIC = (short) 0x4345; // "CE"
    static final byte VERSION = 1; // good practice on API?
    static final int HEADER_LEN = 2 + 1 + 2;
    static final int CRC_LEN = 4;

    private final JsonMapper mapper; // new ObjectMapper() en el constructor,
    // + mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false) para no romper con
    // peers nuevos

    public UdpCodec() {
        this.mapper = JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();
    }

    public Optional<byte[]> encode(MessageEnvelope env) {
        if (env == null) {
            return Optional.empty();
        }
        try {
            byte[] jsonBytes = mapper.writeValueAsBytes(env);

            if (jsonBytes.length > 0xFFFF) {
                System.err.println("[UdpCodec] encode: message too long: " + jsonBytes.length);
                return Optional.empty();
            }
            CRC32 crc = new CRC32();
            crc.update(jsonBytes);
            ByteBuffer bb = ByteBuffer.allocate(jsonBytes.length + 9);
            bb.putShort(MAGIC);
            bb.put(VERSION);
            bb.putShort((short) jsonBytes.length);

            bb.put(jsonBytes);

            // crc32 holds 32 bits (the "32")
            // investigating, getValue returns long only
            // because java has no unsigned int.
            bb.putInt((int) crc.getValue());

            return Optional.of(bb.array());
        } catch (Exception e) {
            System.err.println("[UdpCodec] encode: ERROR " + e);
            return Optional.empty();
        }
    }

    public Optional<MessageEnvelope> decode(byte[] data, int length) {
        if (length < 9 || data == null) {
            return Optional.empty();
        }

        ByteBuffer bb = ByteBuffer.wrap(data, 0, length);

        if (bb.getShort() != MAGIC) {
            return Optional.empty();
        }

        if (bb.get() != VERSION) {
            return Optional.empty();
        }

        // why & 0xFFFF?
        // so when casting to int, we dont lose the abs value
        // (cf. two's complement)
        int jsonLen = bb.getShort() & 0xFFFF;
        if (jsonLen != length - (HEADER_LEN + CRC_LEN)) {
            return Optional.empty();
        }

        byte[] json = new byte[jsonLen];
        bb.get(json);
        CRC32 check = new CRC32();
        check.update(json);

        if ((int) check.getValue() != bb.getInt())
            return Optional.empty();
        try {
            MessageEnvelope env = mapper.readValue(json, MessageEnvelope.class);
            if (env.type() == null || env.from() == null) {
                return Optional.empty();
            }
            return Optional.of(env);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
