package com.nus.sellr.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClearCartRequest {
    private String userId;

    public ClearCartRequest() {}
    public ClearCartRequest(String userId) { this.userId = userId; }
}
