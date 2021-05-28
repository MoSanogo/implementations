package com.implementations.implementations.controllers.payloads;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class ApiResponse {
    private boolean success;
    private String email;
    private String token;


    public ApiResponse(boolean success, String email, String token) {
        this.success = success;
        this.email = email;
        this.token = token;
    }
}
