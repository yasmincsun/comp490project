package com.musicApp.backend.features.authentication.dto;

public class AuthenticationResponseBody {
    private final String token;
    private final String message;
    private final String username;
    private final String email;
    private final Boolean loginStatus;

    public AuthenticationResponseBody(String token, String message) {
        this.token = token;
        this.message = message;
        this.username = null;
        this.email = null;
        this.loginStatus = null;
    }

   public AuthenticationResponseBody(String token, String message, String username, String email, Boolean loginStatus) {
        this.token = token;
        this.message = message;
        this.username = username;
        this.email = email;
        this.loginStatus = loginStatus;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getLoginStatus() {
        return loginStatus;
    }
}