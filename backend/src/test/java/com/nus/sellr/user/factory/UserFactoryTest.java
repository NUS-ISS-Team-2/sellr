package com.nus.sellr.user.factory;

import com.nus.sellr.user.entity.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    private final UserFactory userFactory = new UserFactory();

    @Test
    void testCreateAdminUser() {
        User user = userFactory.createUser("adminUser", "admin@example.com", "password123", Role.ADMIN);

        assertTrue(user instanceof Admin);
        assertEquals("adminUser", user.getUsername());
        assertEquals("admin@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testCreateBuyerUser() {
        User user = userFactory.createUser("buyerUser", "buyer@example.com", "password123", Role.BUYER);

        assertTrue(user instanceof Buyer);
        Buyer buyer = (Buyer) user;
        assertEquals("buyerUser", buyer.getUsername());
        assertEquals("buyer@example.com", buyer.getEmail());
        assertEquals(0, buyer.getLoyaltyPoints()); // initialized to 0
    }

    @Test
    void testCreateSellerUser() {
        User user = userFactory.createUser("sellerUser", "seller@example.com", "password123", Role.SELLER);

        assertTrue(user instanceof Seller);
        Seller seller = (Seller) user;
        assertEquals("sellerUser", seller.getUsername());
        assertEquals("seller@example.com", seller.getEmail());
        assertEquals("My New Store", seller.getStoreName()); // default store name
    }

    @Test
    void testInvalidRoleThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userFactory.createUser("testUser", "test@example.com", "password123", null)
        );

        assertTrue(exception.getMessage().contains("Role is invalid or null"));
    }
}
