package com.nus.sellr.user.dto;

import lombok.Data;

@Data
public class UserRequest {

    private String username;
    private String email;
    private String password;

    public UserRequest() {
    }

    public UserRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
