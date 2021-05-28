package com.implementations.implementations.controllers.payloads;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class AuthResponse {
    private String accessToken;
    private String tokenTYpe="Bearer";

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
