package com.nus.sellr.product.dto;

public class ProductRequest {
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String category;
    private int stock;

    public ProductRequest() {
        // default constructor (needed for JSON deserialization)
    }

    public ProductRequest(String name, String description, double price, String imageUrl, String category, int stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public int getStock() { return stock; }

    public void setStock(int stock) { this.stock = stock; }
}
