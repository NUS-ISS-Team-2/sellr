package com.nus.sellr.product.service;

import com.nus.sellr.product.dto.ProductRequest;
import com.nus.sellr.product.dto.ProductResponse;
import com.nus.sellr.product.entity.Product;
import com.nus.sellr.product.mapper.ProductMapper;
import com.nus.sellr.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("Product A");

        Product product = new Product();
        Product savedProduct = new Product();
        savedProduct.setId("prod1");

        ProductResponse response = new ProductResponse();
        response.setId("prod1");

        when(productMapper.toProduct(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toResponse(savedProduct)).thenReturn(response);

        ProductResponse result = productService.createProduct(request);

        assertEquals("prod1", result.getId());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testGetAllProducts() {
        Product p1 = new Product();
        Product p2 = new Product();
        List<Product> products = Arrays.asList(p1, p2);

        ProductResponse r1 = new ProductResponse();
        ProductResponse r2 = new ProductResponse();

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toResponse(p1)).thenReturn(r1);
        when(productMapper.toResponse(p2)).thenReturn(r2);

        List<ProductResponse> result = productService.getAllProducts();

        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById_found() {
        Product product = new Product();
        product.setId("prod1");
        ProductResponse response = new ProductResponse();
        response.setId("prod1");

        when(productRepository.findById("prod1")).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse result = productService.getProductById("prod1");

        assertEquals("prod1", result.getId());
    }

    @Test
    void testGetProductById_notFound() {
        when(productRepository.findById("prodX")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.getProductById("prodX"));
    }

    @Test
    void testUpdateProduct() {
        Product existing = new Product();
        existing.setId("prod1");

        ProductRequest request = new ProductRequest();
        request.setName("Updated");
        request.setDescription("Desc");
        request.setPrice(100.0);
        request.setImageUrl("url");
        request.setStock(10);
        request.setCategory("Cat");

        Product updated = new Product();
        updated.setId("prod1");
        ProductResponse response = new ProductResponse();
        response.setId("prod1");
        response.setName("Updated");

        when(productRepository.findById("prod1")).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(updated);
        when(productMapper.toResponse(updated)).thenReturn(response);

        ProductResponse result = productService.updateProduct("prod1", request);

        assertEquals("prod1", result.getId());
        assertEquals("Updated", result.getName());
        verify(productRepository, times(1)).save(existing);
    }

    @Test
    void testDeleteProduct() {
        when(productRepository.existsById("prod1")).thenReturn(true);
        doNothing().when(productRepository).deleteById("prod1");

        productService.deleteProduct("prod1");

        verify(productRepository, times(1)).deleteById("prod1");
    }

    @Test
    void testGetProductsBySellerId() {
        Product p = new Product();
        p.setId("prod1");
        p.setSellerId("seller1");

        ProductResponse response = new ProductResponse();
        response.setId("prod1");

        when(productRepository.findBySellerId("seller1")).thenReturn(List.of(p));
        when(productMapper.toResponse(p)).thenReturn(response);

        List<ProductResponse> result = productService.getProductsBySellerId("seller1");

        assertEquals(1, result.size());
        assertEquals("prod1", result.get(0).getId());
    }

    @Test
    void testGetAllCategories() {
        Product p1 = new Product();
        p1.setCategory("cat1");
        Product p2 = new Product();
        p2.setCategory("cat2");

        when(productRepository.findAllCategories()).thenReturn(List.of(p1, p2));

        List<String> result = productService.getAllCategories();

        assertEquals(2, result.size());
        assertTrue(result.contains("cat1"));
        assertTrue(result.contains("cat2"));
    }
}
