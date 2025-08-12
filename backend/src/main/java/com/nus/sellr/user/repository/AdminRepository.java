package com.nus.sellr.user.repository;

import com.nus.sellr.user.entity.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {
    // Add Admin-specific queries here if needed
}