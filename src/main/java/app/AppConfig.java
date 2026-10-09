package app;

public class AppConfig {
    private final int port;
    private final long usernameCheckTimeoutMs;

    public AppConfig(int port, long usernameCheckTimeoutMs) {
        this.port = port;
        this.usernameCheckTimeoutMs = usernameCheckTimeoutMs;
    }

    public static AppConfig defaults() {
        return new AppConfig(5000, 1500);
    }

    public int getPort() {
        return port;
    }

    public long getUsernameCheckTimeoutMs() {
        return usernameCheckTimeoutMs;
    }
}
