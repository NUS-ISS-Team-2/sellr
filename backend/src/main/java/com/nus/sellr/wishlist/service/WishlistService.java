package com.nus.sellr.wishlist.service;

import com.nus.sellr.product.entity.Product;
import com.nus.sellr.product.repository.ProductRepository;
import com.nus.sellr.wishlist.dto.WishlistDTO;
import com.nus.sellr.wishlist.dto.WishlistItemDTO;
import com.nus.sellr.wishlist.entity.Wishlist;
import com.nus.sellr.wishlist.entity.WishlistItem;
import com.nus.sellr.wishlist.repository.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
    }

    public WishlistDTO addProduct(String userId, String productId) {
        Wishlist wishlist = Optional.ofNullable(wishlistRepository.findByUserId(userId))
                .orElseGet(() -> new Wishlist(userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<WishlistItem> existingItem = wishlist.getItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            throw new RuntimeException("Product already in wishlist");
        } else {
            wishlist.getItems().add(new WishlistItem(productId));
        }

        wishlistRepository.save(wishlist);
        return convertToDTO(wishlist);
    }

    public WishlistDTO removeProduct(String userId, String productId) {
        Wishlist wishlist = Optional.ofNullable(wishlistRepository.findByUserId(userId))
                .orElseThrow(() -> new RuntimeException("Wishlist not found"));

        wishlist.getItems().removeIf(item -> item.getProductId().equals(productId));
        wishlistRepository.save(wishlist);
        return convertToDTO(wishlist);
    }

    public WishlistDTO getWishlist(String userId) {
        Wishlist wishlist = Optional.ofNullable(wishlistRepository.findByUserId(userId))
                .orElse(new Wishlist());
        return convertToDTO(wishlist);
    }

    private WishlistDTO convertToDTO(Wishlist wishlist) {
        List<WishlistItemDTO> itemDTOs = wishlist.getItems().stream()
                .map(item -> {
                    Optional<Product> productOpt = productRepository.findById(item.getProductId());
                    if (productOpt.isPresent()) {
                        Product product = productOpt.get();
                        return new WishlistItemDTO(
                                product.getId(),
                                product.getName(),
                                product.getImageUrl(),
                                product.getPrice()
                        );
                    } else {
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
        return new WishlistDTO(wishlist.getUserId(), itemDTOs);
    }
}
