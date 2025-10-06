package com.nus.sellr.product.service;

import com.nus.sellr.product.dto.ProductReviewDTO;
import com.nus.sellr.product.entity.Product;
import com.nus.sellr.product.entity.ProductReview;
import com.nus.sellr.product.repository.ProductRepository;
import com.nus.sellr.product.repository.ProductReviewRepository;
import com.nus.sellr.user.entity.Buyer;
import com.nus.sellr.user.entity.User;
import com.nus.sellr.user.repository.BuyerRepository;
import com.nus.sellr.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductReviewServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductReviewRepository productReviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BuyerRepository buyerRepository;

    @InjectMocks
    private ProductReviewService productReviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductReviews() {
        Pageable pageable = PageRequest.of(0, 10);
        ProductReview review = new ProductReview();
        review.setId("r1");
        review.setUserId("user1");
        review.setProductId("prod1");

        Page<ProductReview> page = new PageImpl<>(List.of(review));

        Product product = new Product();
        product.setId("prod1");
        product.setName("Product 1");

        User user = new Buyer();
        user.setId("user1");
        user.setUsername("John");

        when(productReviewRepository.findByProductId("prod1", pageable)).thenReturn(page);
        when(buyerRepository.findByIdIn(List.of("user1"))).thenReturn(List.of(user));
        when(productRepository.findAllById(List.of("prod1"))).thenReturn(List.of(product));

        Page<ProductReviewDTO> result = productReviewService.getProductReviews("prod1", pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("r1", result.getContent().get(0).getId());
        assertEquals("John", result.getContent().get(0).getUsername());
        assertEquals("Product 1", result.getContent().get(0).getProductName());
    }

    @Test
    void testCreateReview_success() {
        ProductReviewDTO dto = new ProductReviewDTO();
        dto.setUserId("user1");
        dto.setProductId("prod1");
        dto.setRating(5);
        dto.setDescription("Great product!");

        Product product = new Product();
        product.setId("prod1");
        product.setName("Product 1");

        User user = new Buyer();
        user.setId("user1");
        user.setUsername("John");

        ProductReview saved = new ProductReview();
        saved.setId("r1");
        saved.setUserId("user1");
        saved.setProductId("prod1");
        saved.setRating(5);
        saved.setDescription("Great product!");

        when(productRepository.findById("prod1")).thenReturn(Optional.of(product));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        when(productReviewRepository.existsByProductIdAndUserId("prod1", "user1")).thenReturn(false);
        when(productReviewRepository.save(any(ProductReview.class))).thenReturn(saved);

        ProductReviewDTO result = productReviewService.createReview(dto);

        assertEquals("r1", result.getId());
        assertEquals("John", result.getUsername());
        assertEquals("Product 1", result.getProductName());
        assertEquals(5, result.getRating());
    }

    @Test
    void testCreateReview_alreadyReviewed() {
        ProductReviewDTO dto = new ProductReviewDTO();
        dto.setUserId("user1");
        dto.setProductId("prod1");
        dto.setRating(4);

        Product product = new Product();
        product.setId("prod1");

        User user = new Buyer();
        user.setId("user1");

        when(productRepository.findById("prod1")).thenReturn(Optional.of(product));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));

        when(productReviewRepository.existsByProductIdAndUserId("prod1", "user1")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> productReviewService.createReview(dto));
    }


    @Test
    void testCreateReview_invalidRating() {
        ProductReviewDTO dto = new ProductReviewDTO();
        dto.setUserId("user1");
        dto.setProductId("prod1");
        dto.setRating(6);

        assertThrows(IllegalArgumentException.class, () -> productReviewService.createReview(dto));
    }

    @Test
    void testCreateReview_productNotFound() {
        ProductReviewDTO dto = new ProductReviewDTO();
        dto.setUserId("user1");
        dto.setProductId("prodX");
        dto.setRating(4);

        when(productRepository.findById("prodX")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> productReviewService.createReview(dto));
    }

    @Test
    void testCreateReview_userNotFound() {
        ProductReviewDTO dto = new ProductReviewDTO();
        dto.setUserId("userX");
        dto.setProductId("prod1");
        dto.setRating(4);

        Product product = new Product();
        product.setId("prod1");

        when(productRepository.findById("prod1")).thenReturn(Optional.of(product));
        when(userRepository.findById("userX")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> productReviewService.createReview(dto));
    }
}
