package com.nus.sellr.user.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public abstract class User {

    @Id
    private String id;

    private String username;
    private String email;
    private String password;  // store hashed passwords only!

    // Constructors
    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
