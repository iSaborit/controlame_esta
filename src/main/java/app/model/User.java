package app.model;

import java.net.InetAddress;

public class User {
    private String id;
    private String username;
    private InetAddress ipAddress;
    private int port;
    private long lastSeen;

    public User(String id, String username, InetAddress ipAddress) {
        this.id = id;
        this.username = username;
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) throws InvalidUsernameException {
        if (!UsernameValidator.isValid(username)) {
            throw new InvalidUsernameException("Username not valid");
        }
        this.username = username;
    }

    public InetAddress getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }
}
