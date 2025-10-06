package com.nus.sellr.product.controller;

import com.nus.sellr.product.dto.ProductReviewDTO;
import com.nus.sellr.product.service.ProductReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductReviewControllerTest {

    @Mock
    private ProductReviewService productReviewService;

    @InjectMocks
    private ProductReviewController productReviewController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllReview() {
        String productId = "prod1";
        Pageable pageable = PageRequest.of(0, 10);
        ProductReviewDTO review1 = new ProductReviewDTO();
        review1.setId("r1");
        ProductReviewDTO review2 = new ProductReviewDTO();
        review2.setId("r2");

        Page<ProductReviewDTO> page = new PageImpl<>(List.of(review1, review2));

        when(productReviewService.getProductReviews(productId, pageable)).thenReturn(page);

        ResponseEntity<Page<ProductReviewDTO>> response = productReviewController.getAllReview(productId, pageable);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getContent().size());
        verify(productReviewService, times(1)).getProductReviews(productId, pageable);
    }

    @Test
    void testCreateProductReview() {
        ProductReviewDTO request = new ProductReviewDTO();
        request.setProductId("prod1");
        request.setRating(5);

        ProductReviewDTO created = new ProductReviewDTO();
        created.setId("r1");
        created.setProductId("prod1");
        created.setRating(5);

        when(productReviewService.createReview(request)).thenReturn(created);

        ResponseEntity<ProductReviewDTO> response = productReviewController.createProductReview(request, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("r1", response.getBody().getId());
        assertEquals(5, response.getBody().getRating());
        verify(productReviewService, times(1)).createReview(request);
    }
}
