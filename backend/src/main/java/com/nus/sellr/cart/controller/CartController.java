package com.nus.sellr.cart.controller;

import com.nus.sellr.cart.dto.*;
import com.nus.sellr.cart.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Add product
    @PostMapping("/add")
    public CartDTO addProductToCart(@RequestBody AddToCartRequest request) {
        return cartService.addProductToCart(request.getUserId(), request.getProductId(), request.getQuantity());
    }

    @DeleteMapping("/remove")
    public CartDTO removeProduct(@RequestBody RemoveFromCartRequest request) {
        return cartService.removeProductFromCart(request.getUserId(), request.getProductId());
    }

    // Update quantity
    @PutMapping("/update")
    public CartDTO updateQuantity(@RequestBody UpdateQuantityRequest request) {
        return cartService.updateProductQuantity(request.getUserId(), request.getProductId(), request.getQuantity());
    }

    // Get cart (keep as GET with query param)
    @GetMapping
    public CartDTO getCart(@RequestParam String userId) {
        return cartService.getCartByUserId(userId);
    }

    // Clear cart
    @DeleteMapping("/clear")
    public CartDTO clearCart(@RequestBody ClearCartRequest request) {
        return cartService.clearCart(request.getUserId());
    }
}
