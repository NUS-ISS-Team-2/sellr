package com.nus.sellr.product.controller;

import com.nus.sellr.product.dto.ProductReviewDTO;
import com.nus.sellr.product.service.ProductReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;



@RestController
@RequestMapping("/api/reviews")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    public ProductReviewController(ProductReviewService productReviewService) {
        this.productReviewService = productReviewService;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ProductReviewDTO>> getAllReview(
            @PathVariable String productId,
            @PageableDefault(size = 10, sort = "dateCreated", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(productReviewService.getProductReviews(productId, pageable));
    }

    @PostMapping
    public ResponseEntity<ProductReviewDTO> createProductReview(
            @RequestBody ProductReviewDTO request,
            UriComponentsBuilder uriBuilder) {
        ProductReviewDTO created = productReviewService.createReview(request);
        return ResponseEntity.ok(created);
    }
}
