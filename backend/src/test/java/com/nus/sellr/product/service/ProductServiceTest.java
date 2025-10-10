package com.nus.sellr.product.service;

import com.nus.sellr.product.dto.ProductRequest;
import com.nus.sellr.product.dto.ProductResponse;
import com.nus.sellr.product.entity.Product;
import com.nus.sellr.product.mapper.ProductMapper;
import com.nus.sellr.product.repository.ProductRepository;
import com.nus.sellr.user.entity.Seller;
import com.nus.sellr.user.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Mock
    private SellerRepository sellerRepository;

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
        // Arrange
        Product product = new Product();
        product.setId("prod1");
        product.setSellerId("seller1"); // must set sellerId

        ProductResponse response = new ProductResponse();
        response.setId("prod1");

        when(productRepository.findById("prod1")).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        // Mock sellerRepository
        Seller seller = new Seller();
        seller.setId("seller1");
        seller.setUsername("SellerName");

        when(sellerRepository.findById("seller1")).thenReturn(Optional.of(seller));

        // Act
        ProductResponse result = productService.getProductById("prod1");

        // Assert
        assertEquals("prod1", result.getId());
        assertEquals("SellerName", result.getSellerName()); // now the sellerName is attached
        verify(productRepository, times(1)).findById("prod1");
        verify(productMapper, times(1)).toResponse(product);
        verify(sellerRepository, times(1)).findById("seller1");
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
    void testDeleteProduct_exists() {
        String id = "p1";
        when(productRepository.existsById(id)).thenReturn(true);

        productService.deleteProduct(id);

        verify(productRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteProduct_notExists() {
        String id = "p1";
        when(productRepository.existsById(id)).thenReturn(false);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            productService.deleteProduct(id);
        });

        assertEquals("Product not found with id: p1", ex.getMessage());
        verify(productRepository, never()).deleteById(any());
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

    @Test
    void testGetProductEntityById_exists() {
        Product product = new Product();
        product.setId("p1");
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        Product result = productService.getProductEntityById("p1");

        assertEquals(product, result);
    }

    @Test
    void testGetProductEntityById_notExists() {
        when(productRepository.findById("p1")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            productService.getProductEntityById("p1");
        });

        assertEquals("Product not found", ex.getMessage());
    }

    // saveProduct
    @Test
    void testSaveProduct() {
        Product product = new Product();
        product.setId("p1");

        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.saveProduct(product);

        assertEquals(product, result);
    }

    // search
    @Test
    void testSearch_withQueryAndCategory() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = new Product();
        product.setId("p1");
        List<Product> products = List.of(product);
        List<ProductResponse> responses = List.of(new ProductResponse());

        when(mongoTemplate.find(any(Query.class), eq(Product.class))).thenReturn(products);
        when(mongoTemplate.count(any(Query.class), eq(Product.class))).thenReturn(1L);
        when(productMapper.toResponseList(products)).thenReturn(responses);

        Page<ProductResponse> result = productService.search("phone", "electronics", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(responses, result.getContent());
    }

    @Test
    void testSearch_emptyQueryAndCategory() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = new Product();
        List<Product> products = List.of(product);
        List<ProductResponse> responses = List.of(new ProductResponse());

        when(mongoTemplate.find(any(Query.class), eq(Product.class))).thenReturn(products);
        when(mongoTemplate.count(any(Query.class), eq(Product.class))).thenReturn(1L);
        when(productMapper.toResponseList(products)).thenReturn(responses);

        Page<ProductResponse> result = productService.search(null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(responses, result.getContent());
    }


}
