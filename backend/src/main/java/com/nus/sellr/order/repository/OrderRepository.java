package com.nus.sellr.order.repository;

import com.nus.sellr.order.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);

    @Query("{ 'items': { $elemMatch: { 'sellerId': ?0 } } }")
    List<Order> findOrdersBySellerId(String sellerId);
}
