package com.nus.sellr.product.mapper;

import com.nus.sellr.product.dto.ProductRequest;
import com.nus.sellr.product.dto.ProductResponse;
import com.nus.sellr.product.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public  ProductResponse toResponse(Product product) {
        if (product == null) { return null; }
        // map fields from entity -> DTO
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setCategory(product.getCategory());
        dto.setStock(product.getStock());
        dto.setSellerId(product.getSellerId());
        return dto;
    }

    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Product toProduct(ProductRequest req) {
        if (req == null) { return null; }
        Product entity = new Product();
        // id null; Mongo will generate
        entity.setName(req.getName());
        entity.setDescription(req.getDescription());
        entity.setPrice(req.getPrice());
        entity.setImageUrl(req.getImageUrl());
        entity.setCategory(req.getCategory());
        entity.setStock(req.getStock());
        entity.setSellerId(req.getSellerId());
        return entity;
    }
}
