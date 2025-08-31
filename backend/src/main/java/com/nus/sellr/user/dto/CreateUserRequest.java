package com.nus.sellr.user.dto;

import com.nus.sellr.user.entity.Role;
import lombok.Data;

@Data
public class CreateUserRequest {

    private String username;
    private String email;
    private String password;
    private Role role;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
