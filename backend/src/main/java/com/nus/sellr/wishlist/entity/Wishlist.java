package com.nus.sellr.wishlist.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "wishlists")
public class Wishlist {

    @Id
    private String id;
    private String userId;
    private List<WishlistItem> items = new ArrayList<>();

    public Wishlist() {}

    public Wishlist(String userId) {
        this.userId = userId;
    }
}
