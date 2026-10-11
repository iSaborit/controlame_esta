package app.network.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageEnvelope(int v, String alg, MessageType type, String from, long seq, Long inReplyTo, long sentAt,
        Payload payload) {

    private static MessageEnvelope of(MessageType type, String from, long seq, Long inReplyTo, Payload payload) {
        return new MessageEnvelope(1, "none", type, from, seq, inReplyTo, System.currentTimeMillis(), payload);
    }

    public static MessageEnvelope checkRequest(String from, long seq, String username) {
        return of(MessageType.CHECK_USERNAME_REQ, from, seq, null, Payload.checkRequest(username));
    }

    public static MessageEnvelope taken(String from, long seq, long forSeq, String username, String by) {
        return of(MessageType.USERNAME_TAKEN, from, seq, forSeq, Payload.taken(username, by));
    }

    public static MessageEnvelope announce(String from, long seq, String username) {
        return of(MessageType.PRESENCE_ANNOUNCE, from, seq, null, Payload.announce(username));
    }

    public static MessageEnvelope discover(String from, long seq) {
        return of(MessageType.DISCOVER_CONTACTS_REQ, from, seq, null, Payload.discover());
    }

    public static MessageEnvelope ack(String from, long seq, long forSeq, String username,
            List<Payload.PeerInfo> peers) {
        return of(MessageType.PRESENCE_ACK, from, seq, forSeq, Payload.ack(username, peers));
    }

    public static MessageEnvelope usernameChange(String from, long seq, String oldUsername, String newUsername) {
        return of(MessageType.USERNAME_CHANGED, from, seq, null, Payload.renamed(oldUsername, newUsername));
    }

    public static MessageEnvelope bye(String from, long seq, String username) {
        return of(MessageType.DISCONNECT, from, seq, null, Payload.bye(username));
    }
}
