package com.nus.sellr.order.dto;

import com.nus.sellr.order.entity.OrderStatus;
import com.nus.sellr.order.entity.PaymentDetails;
import com.nus.sellr.order.entity.Address;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponseDTO {
    private String orderId;
    private String userId;
    private List<OrderItemDTO> items;
    private BigDecimal orderPrice;
    private LocalDateTime createdAt;
    private OrderStatus overallStatus;
    private Address address;
    private String paymentMethod;
    private PaymentDetails paymentDetails;
}
