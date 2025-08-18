package com.nus.sellr.user.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String id;
    private String username;
    private String email;

    public LoginResponse() {}

    public LoginResponse(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}
