package com.nus.sellr.product.controller;

import com.nus.sellr.product.dto.ProductRequest;
import com.nus.sellr.product.dto.ProductResponse;
import com.nus.sellr.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("Product A");

        ProductResponse response = new ProductResponse();
        response.setId("prod1");
        response.setName("Product A");

        when(productService.createProduct(request)).thenReturn(response);

        var result = productController.createProduct(request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("prod1", result.getBody().getId());

        verify(productService, times(1)).createProduct(request);
    }

    @Test
    void testGetAllProducts() {
        ProductResponse p1 = new ProductResponse();
        p1.setId("prod1");
        ProductResponse p2 = new ProductResponse();
        p2.setId("prod2");
        List<ProductResponse> products = Arrays.asList(p1, p2);

        when(productService.getAllProducts()).thenReturn(products);

        var result = productController.getAllProducts();

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(2, result.getBody().size());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testGetProductById() {
        ProductResponse response = new ProductResponse();
        response.setId("prod1");
        when(productService.getProductById("prod1")).thenReturn(response);

        var result = productController.getProductById("prod1");

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("prod1", result.getBody().getId());
        verify(productService, times(1)).getProductById("prod1");
    }

    @Test
    void testUpdateProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");

        ProductResponse response = new ProductResponse();
        response.setId("prod1");
        response.setName("Updated Product");

        when(productService.updateProduct("prod1", request)).thenReturn(response);

        var result = productController.updateProduct("prod1", request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Updated Product", result.getBody().getName());
        verify(productService, times(1)).updateProduct("prod1", request);
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productService).deleteProduct("prod1");

        var result = productController.deleteProduct("prod1");

        assertEquals(204, result.getStatusCodeValue());
        verify(productService, times(1)).deleteProduct("prod1");
    }

    @Test
    void testSearchProducts() {
        ProductResponse p1 = new ProductResponse();
        p1.setId("prod1");
        Page<ProductResponse> page = new PageImpl<>(List.of(p1));

        when(productService.search(any(), any(), any())).thenReturn(page);

        var result = productController.searchProducts("query", "cat", 0, 10, "createdAt,desc");

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1, result.getBody().getContent().size());
        verify(productService, times(1)).search(any(), any(), any());
    }

    @Test
    void testGetProductsBySeller() {
        ProductResponse p1 = new ProductResponse();
        p1.setId("prod1");
        when(productService.getProductsBySellerId("seller1")).thenReturn(List.of(p1));

        List<ProductResponse> result = productController.getProductsBySeller("seller1");

        assertEquals(1, result.size());
        verify(productService, times(1)).getProductsBySellerId("seller1");
    }

    @Test
    void testGetCategories() {
        when(productService.getAllCategories()).thenReturn(List.of("cat1", "cat2"));

        List<String> result = productController.getCategories();

        assertEquals(2, result.size());
        verify(productService, times(1)).getAllCategories();
    }
}
