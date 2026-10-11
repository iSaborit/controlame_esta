package app.network;

import java.io.*;

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
        return this.type;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public String getPayload() {
        return this.payload;
    }

    public byte[] serialize() {
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;

        try {
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);

            dos.writeUTF(this.type.name());
            dos.writeUTF(this.senderId != null ? this.senderId : "");
            dos.writeUTF(this.payload != null ? this.payload : "");

            dos.flush();
            return baos.toByteArray();

        } catch (IOException e) {
            System.err.println("[NetworkMessage] serialize: ERROR " + e);
            return null;

        } finally {
            try {
                if (dos != null) {
                    dos.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                System.err.println("[NetworkMessage] serialize: error on close: " + e);
            }
        }
    }

    public static NetworkMessage deserialize(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        ByteArrayInputStream bais = null;
        DataInputStream dis = null;

        try {
            bais = new ByteArrayInputStream(data);
            dis = new DataInputStream(bais);

            String typeStr = dis.readUTF();
            MessageType type = MessageType.valueOf(typeStr);

            String senderId = dis.readUTF();
            String payload = dis.readUTF();

            return new NetworkMessage(type, senderId, payload);

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("[NetworkMessage] deserialize: ERROR " + e);
            return null;

        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
                if (bais != null) {
                    bais.close();
                }
            } catch (IOException e) {
                System.err.println("[NetworkMessage] deserialize: error on close: " + e);
            }
        }
    }
}
