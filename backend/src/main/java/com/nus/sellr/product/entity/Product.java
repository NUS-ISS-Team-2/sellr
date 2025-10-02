package com.nus.sellr.product.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Getter
@Setter
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;
    private int stock;
    private String sellerId;

    public Product() {
    }

    public Product(String name, String description, double price, String imageUrl, String category,
                   int stock, String sellerId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.stock = stock;
        this.sellerId = sellerId;
    }
}
