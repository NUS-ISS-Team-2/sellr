package com.nus.sellr.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {
    private String userId;
    private String productId;
    private int quantity;
}