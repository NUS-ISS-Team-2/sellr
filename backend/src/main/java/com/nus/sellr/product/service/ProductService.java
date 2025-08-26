package com.nus.sellr.product.service;

import com.nus.sellr.product.dto.ProductRequest;
import com.nus.sellr.product.dto.ProductResponse;
import com.nus.sellr.product.entity.Product;
import com.nus.sellr.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;

    public ProductService(ProductRepository productRepository, MongoTemplate mongoTemplate) {
        this.productRepository = productRepository;
        this.mongoTemplate = mongoTemplate;
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

        return this.toResponse(savedProduct);
    }

    // Get all products
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponse> responses = new ArrayList<>();

        for (Product p : products) {
            responses.add(this.toResponse(p));
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
        return this.toResponse(p);
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

        return this.toResponse(updated);
    }

    // Delete product
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // Search product
    public Page<ProductResponse> search(String q, String category, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            String regex = ".*" + Pattern.quote(q.trim()) + ".*";
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where("name").regex(regex, "i"),
                    Criteria.where("description").regex(regex, "i")
            ));
        }

        if (category != null && !category.isBlank()) {
            criteriaList.add(Criteria.where("category").is(category));
        }

        Criteria criteria = new Criteria();
        if (!criteriaList.isEmpty()) {
            criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }

        Query query = new Query(criteria).with(pageable);

        // fetch data
        List<Product> products = mongoTemplate.find(query, Product.class);

        // count for pagination
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Product.class);

        // map to DTO
        List<ProductResponse> responses = products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, total);
    }

    /**
     * Maps the Product entity to ProductResponse DTO.
     * @param product
     * @return ProductResponse
     */
    private ProductResponse toResponse(Product product) {
        // map fields from entity -> DTO
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        // dto.setCategory(product.getCategory());
        // dto.setStock(product.getStock());
        return dto;
    }
}
