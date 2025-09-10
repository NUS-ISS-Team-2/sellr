package com.nus.sellr.order.dto;

import com.nus.sellr.order.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderItemResponseDTO {
    private String productId;
    private int quantity;
    private BigDecimal shippingFee;
    private LocalDateTime deliveryDate;
    private OrderStatus status;
    private Integer rating;
    private String review;
}