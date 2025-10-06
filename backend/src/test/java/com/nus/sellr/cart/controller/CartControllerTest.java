package com.nus.sellr.cart.controller;

import com.nus.sellr.cart.dto.*;
import com.nus.sellr.cart.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProductToCart() {
        AddToCartRequest request = new AddToCartRequest();
        request.setUserId("user1");
        request.setProductId("p1");
        request.setQuantity(2);

        CartDTO expectedCart = new CartDTO();
        when(cartService.addProductToCart("user1", "p1", 2)).thenReturn(expectedCart);

        CartDTO result = cartController.addProductToCart(request);

        assertEquals(expectedCart, result);
        verify(cartService, times(1)).addProductToCart("user1", "p1", 2);
    }

    @Test
    void testRemoveProduct() {
        CartDTO expectedCart = new CartDTO();

        when(cartService.removeProductFromCart("user1", "p1")).thenReturn(expectedCart);

        CartDTO result = cartController.removeProduct("user1", "p1");

        assertEquals(expectedCart, result);
        verify(cartService, times(1)).removeProductFromCart("user1", "p1");
    }

    @Test
    void testUpdateQuantity() {
        UpdateQuantityRequest request = new UpdateQuantityRequest();
        request.setUserId("user1");
        request.setProductId("p1");
        request.setQuantity(5);

        CartDTO expectedCart = new CartDTO();
        when(cartService.updateProductQuantity("user1", "p1", 5)).thenReturn(expectedCart);

        CartDTO result = cartController.updateQuantity(request);

        assertEquals(expectedCart, result);
        verify(cartService, times(1)).updateProductQuantity("user1", "p1", 5);
    }

    @Test
    void testGetCart() {
        CartDTO expectedCart = new CartDTO();

        when(cartService.getCartByUserId("user1")).thenReturn(expectedCart);

        CartDTO result = cartController.getCart("user1");

        assertEquals(expectedCart, result);
        verify(cartService, times(1)).getCartByUserId("user1");
    }

    @Test
    void testClearCart() {
        ClearCartRequest request = new ClearCartRequest();
        request.setUserId("user1");

        CartDTO expectedCart = new CartDTO();
        when(cartService.clearCart("user1")).thenReturn(expectedCart);

        CartDTO result = cartController.clearCart(request);

        assertEquals(expectedCart, result);
        verify(cartService, times(1)).clearCart("user1");
    }
}
