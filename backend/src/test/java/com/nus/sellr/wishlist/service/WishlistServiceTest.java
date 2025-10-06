package com.nus.sellr.wishlist.service;

import com.nus.sellr.product.entity.Product;
import com.nus.sellr.product.repository.ProductRepository;
import com.nus.sellr.user.entity.Buyer;
import com.nus.sellr.wishlist.dto.WishlistDTO;
import com.nus.sellr.wishlist.entity.Wishlist;
import com.nus.sellr.wishlist.entity.WishlistItem;
import com.nus.sellr.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private WishlistService wishlistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- addProduct() ---

    @Test
    void testAddProduct_newWishlist_success() {
        // Arrange
        String userId = "user1";
        String productId = "prod1";

        Product product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setPrice(10);
        product.setImageUrl("img.png");

        when(wishlistRepository.findByUserId(userId)).thenReturn(null);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        WishlistDTO result = wishlistService.addProduct(userId, productId);

        // Assert
        assertEquals(userId, result.getUserId());
        assertEquals(1, result.getItems().size());
        assertEquals("Test Product", result.getItems().get(0).getName());
        verify(wishlistRepository, times(1)).save(any(Wishlist.class));
    }

    @Test
    void testAddProduct_existingItem_throwsException() {
        String userId = "user1";
        String productId = "prod1";

        Wishlist wishlist = new Wishlist(userId);
        wishlist.getItems().add(new WishlistItem(productId));

        Product product = new Product();
        product.setId(productId);

        when(wishlistRepository.findByUserId(userId)).thenReturn(wishlist);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act + Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> wishlistService.addProduct(userId, productId));

        assertEquals("Product already in wishlist", ex.getMessage());
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    void testAddProduct_productNotFound_throwsException() {
        String userId = "user1";
        String productId = "invalid";

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> wishlistService.addProduct(userId, productId));

        assertEquals("Product not found", ex.getMessage());
    }

    // --- removeProduct() ---

    @Test
    void testRemoveProduct_success() {
        String userId = "user1";
        String productId = "prod1";

        Wishlist wishlist = new Wishlist(userId);
        wishlist.getItems().add(new WishlistItem(productId));

        when(wishlistRepository.findByUserId(userId)).thenReturn(wishlist);
        when(productRepository.findById(anyString())).thenReturn(Optional.of(new Product()));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        WishlistDTO result = wishlistService.removeProduct(userId, productId);

        assertEquals(userId, result.getUserId());
        assertTrue(result.getItems().isEmpty());
        verify(wishlistRepository, times(1)).save(wishlist);
    }

    @Test
    void testRemoveProduct_wishlistNotFound_throwsException() {
        when(wishlistRepository.findByUserId("user1")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> wishlistService.removeProduct("user1", "prod1"));

        assertEquals("Wishlist not found", ex.getMessage());
    }

    // --- getWishlist() ---

    @Test
    void testGetWishlist_existingWishlist() {
        String userId = "user1";
        Wishlist wishlist = new Wishlist(userId);
        wishlist.getItems().add(new WishlistItem("prod1"));

        Product product = new Product();
        product.setId("prod1");
        product.setName("Product 1");
        product.setImageUrl("img.png");
        product.setPrice(5);

        when(wishlistRepository.findByUserId(userId)).thenReturn(wishlist);
        when(productRepository.findById("prod1")).thenReturn(Optional.of(product));

        WishlistDTO result = wishlistService.getWishlist(userId);

        assertEquals(userId, result.getUserId());
        assertEquals(1, result.getItems().size());
        assertEquals("Product 1", result.getItems().get(0).getName());
    }

    @Test
    void testGetWishlist_noExistingWishlist_returnsEmpty() {
        String userId = "user1";
        when(wishlistRepository.findByUserId(userId)).thenReturn(new Wishlist("user1"));

        WishlistDTO result = wishlistService.getWishlist(userId);

        assertEquals(userId, result.getUserId());
        assertTrue(result.getItems().isEmpty());
    }

}
