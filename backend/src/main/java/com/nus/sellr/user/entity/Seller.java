package com.nus.sellr.user.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "sellers")
public class Seller extends User {

    private String storeName;

    public Seller() {}

    //to extend further for products etc in the future
    public Seller(String username, String email, String password, String storeName) {
        super(username, email, password);
        this.storeName = storeName;
    }
}
