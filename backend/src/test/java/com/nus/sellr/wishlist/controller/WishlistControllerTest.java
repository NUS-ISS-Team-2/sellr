package com.nus.sellr.wishlist.controller;

import com.nus.sellr.wishlist.dto.AddToWishlistRequest;
import com.nus.sellr.wishlist.dto.WishlistDTO;
import com.nus.sellr.wishlist.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WishlistControllerTest {

    @Mock
    private WishlistService wishlistService;

    @InjectMocks
    private WishlistController wishlistController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddItem() {
        // Arrange
        AddToWishlistRequest req = new AddToWishlistRequest();
        req.setUserId("user123");
        req.setProductId("prod456");

        WishlistDTO mockResponse = new WishlistDTO();
        when(wishlistService.addProduct("user123", "prod456")).thenReturn(mockResponse);

        // Act
        ResponseEntity<WishlistDTO> response = wishlistController.addItem(req);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(wishlistService, times(1)).addProduct("user123", "prod456");
    }

    @Test
    void testRemoveItem() {
        // Arrange
        String userId = "user123";
        String productId = "prod456";

        WishlistDTO mockResponse = new WishlistDTO();
        when(wishlistService.removeProduct(userId, productId)).thenReturn(mockResponse);

        // Act
        ResponseEntity<WishlistDTO> response = wishlistController.removeItem(userId, productId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(wishlistService, times(1)).removeProduct(userId, productId);
    }

    @Test
    void testGetWishlist() {
        // Arrange
        String userId = "user123";
        WishlistDTO mockResponse = new WishlistDTO();
        when(wishlistService.getWishlist(userId)).thenReturn(mockResponse);

        // Act
        ResponseEntity<WishlistDTO> response = wishlistController.getWishlist(userId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(wishlistService, times(1)).getWishlist(userId);
    }
}
