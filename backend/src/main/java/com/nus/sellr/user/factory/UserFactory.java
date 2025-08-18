package com.nus.sellr.user.factory;

import com.nus.sellr.user.entity.*;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public User createUser(String username, String email, String password, Role role) {
        switch (role) {
            case ADMIN:
                return new Admin(username, email, password);

            case BUYER:
                return new Buyer(username, email, password, 0); // loyalty points start at 0

            case SELLER:
                return new Seller(username, email, password, "My New Store");

            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}