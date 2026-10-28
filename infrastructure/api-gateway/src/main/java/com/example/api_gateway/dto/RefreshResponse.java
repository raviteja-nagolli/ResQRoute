package com.example.api_gateway.dto;

public class RefreshResponse {
    private String accessToken;

    public RefreshResponse() {
    }

    public RefreshResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
