package com.nus.sellr.product.repository;

import com.nus.sellr.product.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findBySellerId(String sellerId);

    @Query(value = "{}", fields = "{ 'category' : 1 }")
    List<Product> findAllCategories();

}
