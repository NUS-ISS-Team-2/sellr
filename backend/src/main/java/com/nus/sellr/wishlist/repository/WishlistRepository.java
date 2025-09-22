package com.nus.sellr.wishlist.repository;

import com.nus.sellr.wishlist.entity.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WishlistRepository extends MongoRepository<Wishlist, String> {
    Wishlist findByUserId(String userId);

}
