package app;

public class AppConfig {
    private final int port;
    private final long usernameCheckTimeoutMs; // 3 seconds maximum asked
    private final long discoveryTimeoutMs; // 5 seconds maximum asked

    public AppConfig(int port, long usernameCheckTimeoutMs, long discoveryTimeoutMs) {
        this.port = port;
        this.usernameCheckTimeoutMs = usernameCheckTimeoutMs;
        this.discoveryTimeoutMs = discoveryTimeoutMs;
    }

    public static AppConfig defaults() {
        return new AppConfig(5000, 1500, 3000);
    }

    public int getPort() {
        return port;
    }

    public long getUsernameCheckTimeoutMs() {
        return usernameCheckTimeoutMs;
    }

    public long getDiscoveryTimeoutMs() {
        return this.discoveryTimeoutMs;
    }
}
