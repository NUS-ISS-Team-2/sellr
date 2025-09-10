package com.nus.sellr.order.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderItem {
    private String productId;
    private int quantity;
    private Integer rating;
    private BigDecimal shippingFee;
    private String review;
    private LocalDateTime deliveryDate;
    private OrderStatus status;

    public OrderItem() {}

    public OrderItem(String productId, int quantity, BigDecimal shippingFee) {
        this.productId = productId;
        this.quantity = quantity;
        this.shippingFee = shippingFee;
        this.status = OrderStatus.PENDING;

    }
}
