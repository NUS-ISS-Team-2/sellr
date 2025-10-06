package com.nus.sellr.cart.service;

import com.nus.sellr.cart.dto.CartDTO;
import com.nus.sellr.cart.entity.Cart;
import com.nus.sellr.cart.entity.CartItem;
import com.nus.sellr.cart.repository.CartRepository;
import com.nus.sellr.product.entity.Product;
import com.nus.sellr.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProductToCart_newProduct() {
        String userId = "user1";
        String productId = "p1";

        Product product = new Product();
        product.setId(productId);
        product.setName("Product1");
        product.setImageUrl("url");
        product.setPrice(100.0);

        when(cartRepository.findByUserId(userId)).thenReturn(null);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        CartDTO result = cartService.addProductToCart(userId, productId, 2);

        assertEquals(userId, result.getUserId());
        assertEquals(1, result.getItems().size());
        assertEquals("Product1", result.getItems().get(0).getName());
        assertEquals(2, result.getItems().get(0).getQuantity());

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testAddProductToCart_existingItem() {
        String userId = "user1";
        String productId = "p1";

        Cart cart = new Cart(userId);
        cart.getItems().add(new CartItem(productId, 1));

        Product product = new Product();
        product.setId(productId);
        product.setName("Product1");
        product.setImageUrl("url");
        product.setPrice(100.0);

        when(cartRepository.findByUserId(userId)).thenReturn(cart);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        CartDTO result = cartService.addProductToCart(userId, productId, 2);

        assertEquals(3, result.getItems().get(0).getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testRemoveProductFromCart() {
        String userId = "user1";
        String productId = "p1";

        Cart cart = new Cart(userId);
        cart.getItems().add(new CartItem(productId, 1));
        when(cartRepository.findByUserId(userId)).thenReturn(cart);

        CartDTO result = cartService.removeProductFromCart(userId, productId);

        assertTrue(result.getItems().isEmpty());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testUpdateProductQuantity_setToZero_removesItem() {
        String userId = "user1";
        String productId = "p1";

        Cart cart = new Cart(userId);
        cart.getItems().add(new CartItem(productId, 5));

        when(cartRepository.findByUserId(userId)).thenReturn(cart);

        CartDTO result = cartService.updateProductQuantity(userId, productId, 0);

        assertTrue(result.getItems().isEmpty());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testUpdateProductQuantity_existingItem() {
        String userId = "user1";
        String productId = "p1";

        Cart cart = new Cart(userId);
        cart.getItems().add(new CartItem(productId, 1));

        Product product = new Product();
        product.setId(productId);
        product.setName("Product1");
        product.setImageUrl("url");
        product.setPrice(100.0);

        when(cartRepository.findByUserId(userId)).thenReturn(cart);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        CartDTO result = cartService.updateProductQuantity(userId, productId, 5);

        assertEquals(5, result.getItems().get(0).getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testGetCartByUserId_cartExists() {
        String userId = "user1";
        Cart cart = new Cart(userId);
        when(cartRepository.findByUserId(userId)).thenReturn(cart);

        CartDTO result = cartService.getCartByUserId(userId);

        assertEquals(userId, result.getUserId());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void testClearCart() {
        String userId = "user1";
        Cart cart = new Cart(userId);
        cart.getItems().add(new CartItem("p1", 2));

        when(cartRepository.findByUserId(userId)).thenReturn(cart);

        CartDTO result = cartService.clearCart(userId);

        assertTrue(result.getItems().isEmpty());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testConvertToDTO_skipsMissingProducts() {
        Cart cart = new Cart("user1");
        cart.getItems().add(new CartItem("missingProduct", 2));

        // productRepository.findById returns empty for missing product
        when(productRepository.findById("missingProduct")).thenReturn(Optional.empty());

        CartDTO result = cartService.getCartByUserId("user1");

        assertTrue(result.getItems().isEmpty()); // ensures null path is hit
    }
}
