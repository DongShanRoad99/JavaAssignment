package com.doc.auth.model;

public class AuthPayload {
    private String accessToken;
    private User user;

    public AuthPayload(String accessToken, User user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    // getters and setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
} 