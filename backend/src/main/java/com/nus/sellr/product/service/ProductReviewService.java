package com.nus.sellr.product.service;

import com.nus.sellr.product.dto.ProductReviewDTO;
import com.nus.sellr.product.entity.Product;
import com.nus.sellr.product.entity.ProductReview;
import com.nus.sellr.product.repository.ProductRepository;
import com.nus.sellr.product.repository.ProductReviewRepository;
import com.nus.sellr.user.entity.User;
import com.nus.sellr.user.repository.BuyerRepository;
import com.nus.sellr.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProductReviewService {

    private final ProductRepository productRepository;
    private final ProductReviewRepository productReviewRepository;
    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;

    public ProductReviewService(ProductRepository productRepository,
                                ProductReviewRepository productReviewRepository,
                                UserRepository userRepository,
                                BuyerRepository buyerRepository) {
        this.productRepository = productRepository;
        this.productReviewRepository = productReviewRepository;
        this.userRepository = userRepository;
        this.buyerRepository = buyerRepository;
    }

    public Page<ProductReviewDTO> getProductReviews(String productId, Pageable pageable) {
        Page<ProductReview> page = productReviewRepository.findByProductId(productId, pageable);

        // Batch load users and products to avoid N+1 lookups
        List<String> userIds = page.getContent().stream()
                .map(ProductReview::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<String, User> usersById = buyerRepository.findByIdIn(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));


        List<String> productIds = page.getContent().stream()
                .map(ProductReview::getProductId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<String, Product> productsById = StreamSupport
                .stream(productRepository.findAllById(productIds).spliterator(), false)
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return page.map(pr -> toDto(pr,
                usersById.get(pr.getUserId()),
                productsById.get(pr.getProductId())));
    }

    public ProductReviewDTO createReview(ProductReviewDTO dto) {
        if (dto.getUserId() == null || dto.getUserId().isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
        if (dto.getProductId() == null || dto.getProductId().isBlank()) {
            throw new IllegalArgumentException("productId is required");
        }
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("rating must be between 1 and 5");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (productReviewRepository.existsByProductIdAndUserId(dto.getProductId(), dto.getUserId())) {
            throw new IllegalStateException("You have already reviewed this product.");
        }

        ProductReview entity = new ProductReview();
        entity.setProductId(dto.getProductId());
        entity.setUserId(dto.getUserId());
        entity.setRating(dto.getRating());
        entity.setDescription(dto.getDescription());
        entity.setDateCreated(LocalDateTime.now());
        entity.setCreatedBy(dto.getUserId());

        ProductReview saved = productReviewRepository.save(entity);
        return toDto(saved, user, product);
    }

    private ProductReviewDTO toDto(ProductReview entity, User user, Product product) {
        ProductReviewDTO dto = new ProductReviewDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setUsername(user != null ? user.getUsername() : null);
        dto.setProductId(entity.getProductId());
        dto.setProductName(product != null ? product.getName() : null);
        dto.setRating(entity.getRating());
        dto.setDescription(entity.getDescription());
        dto.setCreatedDate(entity.getDateCreated());
        return dto;
    }
}
