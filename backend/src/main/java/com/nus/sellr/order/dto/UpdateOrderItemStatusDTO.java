package com.nus.sellr.order.dto;

import com.nus.sellr.order.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateOrderItemStatusDTO {
    private String orderId;
    private String productId;
    private OrderStatus status;
    private LocalDateTime deliveryDate; // optional
}