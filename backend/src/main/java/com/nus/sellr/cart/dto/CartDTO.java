package com.nus.sellr.cart.dto;

import java.util.List;

public class CartDTO {
    private String userId;
    private List<CartItemDTO> items;

    public CartDTO() {}

    public CartDTO(String userId, List<CartItemDTO> items) {
        this.userId = userId;
        this.items = items;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<CartItemDTO> getItems() { return items; }
    public void setItems(List<CartItemDTO> items) { this.items = items; }
}
