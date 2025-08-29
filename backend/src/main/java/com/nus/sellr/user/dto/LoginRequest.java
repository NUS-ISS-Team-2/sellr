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
    }

    public LoginRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }
}
