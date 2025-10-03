package com.nus.sellr.product.controller;

import com.nus.sellr.product.dto.ProductRequest;
import com.nus.sellr.product.dto.ProductResponse;
import com.nus.sellr.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")

public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Create a new product
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        ProductResponse createdProduct = productService.createProduct(request);
        return ResponseEntity.ok(createdProduct);
    }

    // Get all products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // Update product by ID
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @RequestBody ProductRequest request) {
        ProductResponse updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }

    // Delete product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // 🔎 Search / Browse (filters + sort + pagination)
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort      // e.g. "price,asc" or "name,asc"
    ) {
        String[] s = sort.split(",", 2);
        Sort springSort = (s.length == 2) ?
                Sort.by(Sort.Direction.fromString(s[1]), s[0]) : Sort.by("createdAt").descending();

        Pageable pageable = PageRequest.of(page, size, springSort);
        Page<ProductResponse> results = productService.search(q, category, pageable);
        return ResponseEntity.ok(results);
    }

    // Get products by seller
    @GetMapping("/my-products")
    public List<ProductResponse> getProductsBySeller(@RequestParam String sellerId) {
        return productService.getProductsBySellerId(sellerId);
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return productService.getAllCategories();
    }
}
