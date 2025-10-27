package org.example;

public class SessionInfo {
    private final String username;
    private final long createdAt;

    public SessionInfo(String username) {
        this.username = username;
        this.createdAt = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
