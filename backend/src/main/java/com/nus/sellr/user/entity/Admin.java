package com.nus.sellr.user.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "admins")
public class Admin extends User {

    private String adminCode; //not sure if we want this here, just something to diffentiate as an admin

    public Admin() {}

    public Admin(String username, String email, String password, String adminCode) {
        super(username, email, password);
        this.adminCode = adminCode;
    }
}