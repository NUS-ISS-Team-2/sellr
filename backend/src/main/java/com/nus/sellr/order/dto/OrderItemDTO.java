package com.nus.sellr.order.dto;

import com.nus.sellr.order.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderItemDTO {
    private String productId;
    private String productName;
    private String imageUrl;
    private int quantity;
    private BigDecimal shippingFee;
    private OrderStatus status;
    private LocalDateTime deliveryDate;
    private Integer rating;
    private String review;
}
