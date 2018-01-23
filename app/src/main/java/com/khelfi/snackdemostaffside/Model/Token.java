package com.khelfi.snackdemostaffside.Model;

/**
 * Token model, to properly manage notifications
 * from Firebase Cloud Messaging
 *
 * Created by norma on 22/01/2018.
 */

public class Token {

    private String token;
    private Boolean isServerToken;

    public Token() {
    }

    public Token(String token, Boolean isServerToken) {
        this.token = token;
        this.isServerToken = isServerToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getIsServerToken() {
        return isServerToken;
    }

    public void setIsServerToken(Boolean isServerToken) {
        this.isServerToken = isServerToken;
    }
}
