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
}
