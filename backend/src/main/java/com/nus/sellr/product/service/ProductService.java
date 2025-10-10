package com.nus.sellr.product.service;

import com.nus.sellr.product.dto.ProductRequest;
import com.nus.sellr.product.dto.ProductResponse;
import com.nus.sellr.product.entity.Product;
import com.nus.sellr.product.mapper.ProductMapper;
import com.nus.sellr.product.repository.ProductRepository;
import com.nus.sellr.user.repository.SellerRepository;
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
    private final ProductMapper productMapper;
    private final SellerRepository sellerRepository;

    public ProductService(
            ProductRepository productRepository,
            MongoTemplate mongoTemplate,
            ProductMapper productMapper, SellerRepository sellerRepository) {
        this.productRepository = productRepository;
        this.mongoTemplate = mongoTemplate;
        this.productMapper = productMapper;
        this.sellerRepository = sellerRepository;
    }

    // Create new product
    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toProduct(request);

        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    // Get all products
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponse> responses = new ArrayList<>();

        for (Product p : products) {
            responses.add(productMapper.toResponse(p));
        }

        return responses;
    }

    // Get single product by ID
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        ProductResponse response = productMapper.toResponse(product);

        // ✅ Fetch and attach seller name
        sellerRepository.findById(product.getSellerId())
                .ifPresentOrElse(
                        seller -> response.setSellerName(seller.getUsername()),
                        () -> response.setSellerName("Unknown Seller"));
        return response;
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
        existing.setStock(request.getStock());
        existing.setCategory(request.getCategory());

        Product updated = productRepository.save(existing);

        return productMapper.toResponse(updated);
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
                    Criteria.where("description").regex(regex, "i")));
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
        List<ProductResponse> responses = productMapper.toResponseList(products);

        return new PageImpl<>(responses, pageable, total);
    }

    public Product getProductEntityById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<ProductResponse> getProductsBySellerId(String sellerId) {
        return productRepository.findBySellerId(sellerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getCategory(),
                product.getStock(),
                product.getSellerId()
        );
    }

    public List<String> getAllCategories() {
        // Get products with only category populated, then extract distinct categories
        return productRepository.findAllCategories()
                .stream()
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }
}
