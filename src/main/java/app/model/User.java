package app.model;

import java.net.InetAddress;

public class User {
    private String id;
    private String username;
    private InetAddress ipAddress;

    public User(String id, String username, InetAddress ipAddress) {
        this.id = id;
        this.username = username;
        this.ipAddress = ipAddress;
    }

    public String getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        // LAYOUT ONLY
        this.username = username;
    }

    public InetAddress getIpAddress() {
        return this.ipAddress;
    }
}
