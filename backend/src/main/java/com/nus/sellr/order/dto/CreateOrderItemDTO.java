package com.nus.sellr.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateOrderItemDTO {
    private String productId;
    private int quantity;
    private BigDecimal shippingFee;
}