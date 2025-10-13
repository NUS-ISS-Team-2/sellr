package com.nus.sellr.cart.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddToCartRequestTest {

    @Test
    void testSettersAndGetters() {
        AddToCartRequest request = new AddToCartRequest();
        request.setUserId("user1");
        request.setProductId("prod1");
        request.setQuantity(3);

        assertEquals("user1", request.getUserId());
        assertEquals("prod1", request.getProductId());
        assertEquals(3, request.getQuantity());
    }
}
