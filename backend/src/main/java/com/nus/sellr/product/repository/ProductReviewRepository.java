package com.nus.sellr.product.repository;

import com.nus.sellr.product.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductReviewRepository extends MongoRepository<ProductReview, String> {
    Page<ProductReview> findByProductId(String productId, Pageable pageable);
    boolean existsByProductIdAndUserId(String productId, String userId);
}
