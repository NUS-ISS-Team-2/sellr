package com.nus.sellr.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProductReviewDTO {
    private String id;
    private String userId;
    private String username;
    private String productId;
    private String productName;
    private int rating;
    private String description;
    private LocalDateTime createdDate;
}
