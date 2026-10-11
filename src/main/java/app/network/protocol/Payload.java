package app.network.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // Jackson omite los null → paquetes pequeños
public record Payload(String username, String oldUsername, String newUsername, String by, List<PeerInfo> peers) {

    public record PeerInfo(String id, String username, long lastSeen) {
    }

    public static Payload announce(String username) {
        return new Payload(username, null, null, null, null);
    }

    public static Payload checkRequest(String username) {
        return new Payload(username, null, null, null, null);
    }

    public static Payload taken(String username, String by) {
        return new Payload(username, null, null, by, null);
    }

    public static Payload discover() {
        return new Payload(null, null, null, null, null);
    }

    public static Payload ack(String username, List<PeerInfo> peers) {
        return new Payload(username, null, null, null, peers);
    }

    public static Payload renamed(String oldUsername, String newUsername) {
        return new Payload(null, oldUsername, newUsername, null, null);
    }

    public static Payload bye(String username) {
        return new Payload(username, null, null, null, null);
    }
}
