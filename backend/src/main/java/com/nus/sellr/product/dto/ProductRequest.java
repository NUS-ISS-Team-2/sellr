package com.nus.sellr.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;
    private int stock;

    private String sellerId;

    public ProductRequest() {
        // default constructor (needed for JSON deserialization)
    }

    public ProductRequest(String name, String description, double price, String imageUrl,
                          String category, int stock, String sellerId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.stock = stock;
        this.sellerId = sellerId;
    }
}
