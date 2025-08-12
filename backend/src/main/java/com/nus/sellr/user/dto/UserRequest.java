package com.nus.sellr.user.dto;

import com.nus.sellr.user.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserRequest {

    private String username;
    private String email;
    private String password;
    private Role role;

    public UserRequest() {
    }

    public UserRequest(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
