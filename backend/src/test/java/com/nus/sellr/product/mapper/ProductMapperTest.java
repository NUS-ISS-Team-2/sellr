package com.nus.sellr.product.mapper;

import com.nus.sellr.product.dto.ProductRequest;
import com.nus.sellr.product.dto.ProductResponse;
import com.nus.sellr.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductMapper();
    }

    @Test
    void testToResponse_mapsAllFields() {
        Product product = new Product();
        product.setId("p1");
        product.setName("Product 1");
        product.setDescription("Desc");
        product.setPrice(100.0);
        product.setImageUrl("url");
        product.setCategory("cat");
        product.setStock(10);
        product.setSellerId("seller1");

        ProductResponse dto = mapper.toResponse(product);

        assertNotNull(dto);
        assertEquals("p1", dto.getId());
        assertEquals("Product 1", dto.getName());
        assertEquals("Desc", dto.getDescription());
        assertEquals(100.0, dto.getPrice());
        assertEquals("url", dto.getImageUrl());
        assertEquals("cat", dto.getCategory());
        assertEquals(10, dto.getStock());
        assertEquals("seller1", dto.getSellerId());
        assertTrue(dto.isLowStock()); // stock < 20
    }

    @Test
    void testToResponse_returnsNull_whenInputIsNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void testToResponseList_mapsAllItems() {
        Product product1 = new Product();
        product1.setId("p1");
        product1.setName("Product 1");

        Product product2 = new Product();
        product2.setId("p2");
        product2.setName("Product 2");

        List<ProductResponse> dtoList = mapper.toResponseList(List.of(product1, product2));

        assertEquals(2, dtoList.size());
        assertEquals("p1", dtoList.get(0).getId());
        assertEquals("p2", dtoList.get(1).getId());
    }

    @Test
    void testToProduct_mapsAllFields() {
        ProductRequest req = new ProductRequest();
        req.setName("Product 1");
        req.setDescription("Desc");
        req.setPrice(100.0);
        req.setImageUrl("url");
        req.setCategory("cat");
        req.setStock(50);
        req.setSellerId("seller1");

        Product entity = mapper.toProduct(req);

        assertNotNull(entity);
        assertEquals("Product 1", entity.getName());
        assertEquals("Desc", entity.getDescription());
        assertEquals(100.0, entity.getPrice());
        assertEquals("url", entity.getImageUrl());
        assertEquals("cat", entity.getCategory());
        assertEquals(50, entity.getStock());
        assertEquals("seller1", entity.getSellerId());
    }

    @Test
    void testToProduct_returnsNull_whenInputIsNull() {
        assertNull(mapper.toProduct(null));
    }
}
