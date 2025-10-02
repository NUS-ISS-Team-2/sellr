package com.nus.sellr.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddReviewDTO {
    private String orderId;
    private String productId;
    private Integer rating;
    private String review;
}