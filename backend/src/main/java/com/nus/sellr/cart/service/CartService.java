package com.nus.sellr.cart.service;

import com.nus.sellr.cart.dto.CartDTO;
import com.nus.sellr.cart.dto.CartItemDTO;
import com.nus.sellr.cart.entity.Cart;
import com.nus.sellr.cart.entity.CartItem;
import com.nus.sellr.cart.repository.CartRepository;
import com.nus.sellr.product.entity.Product;
import com.nus.sellr.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    // Add product to cart
    public CartDTO addProductToCart(String userId, String productId, int quantity) {
        Cart cart = Optional.ofNullable(cartRepository.findByUserId(userId))
                .orElseGet(() -> new Cart(userId));

        // Ensure product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            cart.getItems().add(new CartItem(productId, quantity));
        }

        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    // Remove product from cart
    public CartDTO removeProductFromCart(String userId, String productId) {
        Cart cart = Optional.ofNullable(cartRepository.findByUserId(userId))
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    // Update product quantity
    public CartDTO updateProductQuantity(String userId, String productId, int quantity) {
        if (quantity <= 0) {
            return removeProductFromCart(userId, productId);
        }

        Cart cart = Optional.ofNullable(cartRepository.findByUserId(userId))
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(quantity);
        } else {
            cart.getItems().add(new CartItem(productId, quantity));
        }

        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    // Get cart for user
    public CartDTO getCartByUserId(String userId) {
        Cart cart = Optional.ofNullable(cartRepository.findByUserId(userId))
                .orElse(new Cart(userId));
        return convertToDTO(cart);
    }

    // Clear cart
    public CartDTO clearCart(String userId) {
        Cart cart = Optional.ofNullable(cartRepository.findByUserId(userId))
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
            .map(item -> {
                Optional<Product> productOpt = productRepository.findById(item.getProductId());
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    return new CartItemDTO(
                        product.getId(),
                        product.getName(),
                        product.getImageUrl(),
                        product.getPrice(),
                        item.getQuantity(), product.getSellerId()
                    );
                } else {
                    // Optionally log missing product
                    return null; // skip this item
                }
            })
            .filter(dto -> dto != null)
            .collect(Collectors.toList());
        return new CartDTO(cart.getUserId(), itemDTOs);
    }
}
