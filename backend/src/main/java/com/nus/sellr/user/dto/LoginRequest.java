package com.nus.sellr.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequest {

    private String password;
    private String identifier;

    public LoginRequest() {
        // Default constructor required by Spring Data and other serialization frameworks
    }
}
