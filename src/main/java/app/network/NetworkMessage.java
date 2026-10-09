package app.network;

public class NetworkMessage {
    private MessageType type;
    private String senderId;
    private String payload;

    public NetworkMessage(MessageType type, String senderId, String payload) {
        this.type = type;
        this.senderId = senderId;
        this.payload = payload;
    }

    public MessageType getType() {
        return null;
    }

    public String getSenderId() {
        return null;
    }

    public String getPayload() {
        return null;
    }

    public byte[] serialize() {
        return null;
    }

    public static NetworkMessage deserialize(byte[] data) {
        return null;
    }
}
