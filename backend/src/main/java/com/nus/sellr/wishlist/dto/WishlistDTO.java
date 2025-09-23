package com.nus.sellr.wishlist.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WishlistDTO {
    private String userId;
    private List<WishlistItemDTO> items;

    public WishlistDTO() {}

    public WishlistDTO(String userId, List<WishlistItemDTO> items) {
        this.userId = userId;
        this.items = items;
    }
}
