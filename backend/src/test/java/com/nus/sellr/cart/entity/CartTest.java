package com.nus.sellr.cart.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    @Test
    void testDefaultConstructorAndSettersGetters() {
        Cart cart = new Cart();

        cart.setId("cart123");
        cart.setUserId("user1");

        List<CartItem> items = new ArrayList<>();
        CartItem item = new CartItem();
        item.setProductId("prod1");
        items.add(item);

        cart.setItems(items);

        assertEquals("cart123", cart.getId());
        assertEquals("user1", cart.getUserId());
        assertEquals(1, cart.getItems().size());
        assertEquals("prod1", cart.getItems().get(0).getProductId());
    }

    @Test
    void testParameterizedConstructor() {
        Cart cart = new Cart("user2");
        assertEquals("user2", cart.getUserId());
        // items list should be initialized
        assertNotNull(cart.getItems());
        assertTrue(cart.getItems().isEmpty());
    }
}
