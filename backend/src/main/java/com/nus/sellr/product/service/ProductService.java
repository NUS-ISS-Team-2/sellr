package com.nus.sellr.product.service;

import com.nus.sellr.product.dto.ProductRequest;
import com.nus.sellr.product.dto.ProductResponse;
import com.nus.sellr.product.entity.Product;
import com.nus.sellr.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Create new product
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getImageUrl()
        );

        Product savedProduct = productRepository.save(product);

        return new ProductResponse(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getPrice(),
                savedProduct.getImageUrl()
        );
    }

    // Get all products
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponse> responses = new ArrayList<>();

        for (Product p : products) {
            responses.add(new ProductResponse(
                    p.getId(),
                    p.getName(),
                    p.getDescription(),
                    p.getPrice(),
                    p.getImageUrl()
            ));
        }

        return responses;
    }

    // Get single product by ID
    public ProductResponse getProductById(String id) {
        Optional<Product> productOpt = productRepository.findById(id);

        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }

        Product p = productOpt.get();
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getImageUrl()
        );
    }

    // Update product
    public ProductResponse updateProduct(String id, ProductRequest request) {
        Optional<Product> productOpt = productRepository.findById(id);

        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }

        Product existing = productOpt.get();
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setImageUrl(request.getImageUrl());

        Product updated = productRepository.save(existing);

        return new ProductResponse(
                updated.getId(),
                updated.getName(),
                updated.getDescription(),
                updated.getPrice(),
                updated.getImageUrl()
        );
    }

    // Delete product
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
