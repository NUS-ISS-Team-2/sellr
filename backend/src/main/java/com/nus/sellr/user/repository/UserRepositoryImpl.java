package com.nus.sellr.user.repository;

import com.nus.sellr.user.entity.Admin;
import com.nus.sellr.user.entity.Buyer;
import com.nus.sellr.user.entity.Seller;
import com.nus.sellr.user.entity.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Optional<User> findByIdentifierAcrossCollections(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return Optional.empty();
        }

        // Build query for email OR username
        Query query = new Query(new Criteria().orOperator(
                Criteria.where("email").is(identifier),
                Criteria.where("username").is(identifier)
        ));

        // List of collection/entity pairs
        List<Object[]> collections = List.of(
                new Object[]{Admin.class, "admins"},
                new Object[]{Seller.class, "sellers"},
                new Object[]{Buyer.class, "buyers"}
        );

        for (Object[] entry : collections) {
            Class<? extends User> clazz = (Class<? extends User>) entry[0];
            String collectionName = (String) entry[1];

            User user = mongoTemplate.findOne(query, clazz, collectionName);
            if (user != null) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }
}
