package com.nus.sellr.user.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "buyers")
public class Buyer extends User {

    private int loyaltyPoints;

    public Buyer() {}

    //to extend further for cart, products etc.
    public Buyer(String username, String email, String password, int loyaltyPoints) {
        super(username, email, password);
        this.loyaltyPoints = loyaltyPoints;
    }
}
