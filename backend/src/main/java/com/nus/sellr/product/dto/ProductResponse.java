package com.nus.sellr.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;
    private int stock;
    private String sellerId;
    private String sellerName;
    private boolean lowStock; // true if stock < 20


    public ProductResponse() {
    }

    public ProductResponse(
            String id,
            String name,
            String description,
            double price,
            String imageUrl,
            String category,
            int stock,
            String sellerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.stock = stock;
        this.sellerId = sellerId;
        this.lowStock = stock < 20; // automatically set lowStock
    }

    public void setStock(int stock) {
        this.stock = stock;
        this.lowStock = stock < 20;
    }
}
