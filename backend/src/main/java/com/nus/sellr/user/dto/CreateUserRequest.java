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
        // Default constructor required by Spring Data and other serialization frameworks
    }
}
