package com.nus.sellr.user.repository;

import com.nus.sellr.user.entity.Seller;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends MongoRepository<Seller, String> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}