package com.nus.sellr.user.repository;

import com.nus.sellr.user.entity.Buyer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRepository extends MongoRepository<Buyer, String> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}