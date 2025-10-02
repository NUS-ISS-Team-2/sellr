package com.nus.sellr.wishlist.controller;

import com.nus.sellr.wishlist.dto.AddToWishlistRequest;
import com.nus.sellr.wishlist.dto.WishlistDTO;
import com.nus.sellr.wishlist.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    public final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    // Add product from wishlist
    @PostMapping("/add")
    public ResponseEntity<WishlistDTO> addItem(@RequestBody AddToWishlistRequest req) {
        WishlistDTO result = wishlistService.addProduct(req.getUserId(), req.getProductId());
        return ResponseEntity.ok(result);
    }

    // Remove product from wishlist
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<WishlistDTO> removeItem(@RequestParam String userId,
                                 @PathVariable String productId) {
        WishlistDTO result = wishlistService.removeProduct(userId, productId);
        return ResponseEntity.ok(result);
    }

    // Get wishlist
    @GetMapping("/getWishlist")
    public ResponseEntity<WishlistDTO> getWishlist(@RequestParam String userId) {
        WishlistDTO result = wishlistService.getWishlist(userId);
        return ResponseEntity.ok(result);
    }
}
