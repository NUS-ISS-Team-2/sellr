package com.nus.sellr.wishlist.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistItem {
    private String productId;  // reference to Product._id

    public WishlistItem() {}

    public WishlistItem(String productId) {
        this.productId = productId;
    }
}
