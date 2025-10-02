package com.nus.sellr.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {
    private String productId;
    private String name;
    private String imageUrl;
    private double price;
    private int quantity;
    private String sellerId;

    public CartItemDTO() {}

    public CartItemDTO(String productId, String name, String imageUrl, double price, int quantity, String sellerId) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.sellerId = sellerId;
    }
}
