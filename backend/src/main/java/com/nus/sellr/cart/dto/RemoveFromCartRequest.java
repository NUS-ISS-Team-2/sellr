package com.nus.sellr.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveFromCartRequest {
    private String userId;
    private String productId;

    // Constructors
    public RemoveFromCartRequest() {}
    public RemoveFromCartRequest(String userId, String productId) {
        this.userId = userId;
        this.productId = productId;
    }
}