package com.nus.sellr.order.repository;

import com.nus.sellr.order.entity.Order;
import com.nus.sellr.product.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);

}
