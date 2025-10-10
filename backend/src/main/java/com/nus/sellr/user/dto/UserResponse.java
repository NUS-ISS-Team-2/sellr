package com.nus.sellr.user.dto;

import com.nus.sellr.user.entity.Role;
import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private Role role;
    private boolean disabled;

    public UserResponse() {}

    public UserResponse(String id, String username, String email, Role role, boolean disabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.disabled = disabled;
    }
}
