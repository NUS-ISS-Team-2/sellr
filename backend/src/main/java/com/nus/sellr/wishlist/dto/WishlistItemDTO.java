package com.nus.sellr.wishlist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistItemDTO {
    private String productId;
    private String name;
    private String imageUrl;
    private double price;

    public WishlistItemDTO() {}

    public WishlistItemDTO(String productId, String name, String imageUrl, double price) {
        this.productId = productId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }
}
