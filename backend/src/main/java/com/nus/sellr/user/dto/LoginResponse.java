package com.nus.sellr.user.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String id;
    private String username;
    private String email;
    private String token;

    public LoginResponse() {}

    public LoginResponse(String id, String username, String email, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.token = token;

    }
}
