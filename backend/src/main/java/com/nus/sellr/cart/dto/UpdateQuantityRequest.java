package com.nus.sellr.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateQuantityRequest {
    private String userId;
    private String productId;
    private int quantity;

    public UpdateQuantityRequest() {}
    public UpdateQuantityRequest(String userId, String productId, int quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
