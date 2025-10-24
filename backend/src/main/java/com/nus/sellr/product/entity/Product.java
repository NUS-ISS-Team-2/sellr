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
    private boolean deleted;


    public Product() {
        // Default constructor required by Spring Data and other serialization frameworks
    }
}
