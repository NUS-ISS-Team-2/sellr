package com.nus.sellr.cart.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UpdateQuantityRequestTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        // Using no-args constructor
        UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setUserId("user1");
        request.setProductId("prod1");
        request.setQuantity(5);

        assertEquals("user1", request.getUserId());
        assertEquals("prod1", request.getProductId());
        assertEquals(5, request.getQuantity());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        // Using all-args constructor
        UpdateQuantityRequest request = new UpdateQuantityRequest("user2", "prod2", 10);

        assertEquals("user2", request.getUserId());
        assertEquals("prod2", request.getProductId());
        assertEquals(10, request.getQuantity());
    }
}
