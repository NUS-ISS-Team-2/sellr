package com.nus.sellr.order.controller;

import com.nus.sellr.order.dto.*;
import com.nus.sellr.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1. Create Order
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody CreateOrderDTO createOrderDTO) {
        OrderResponseDTO createdOrder = orderService.createOrder(createOrderDTO);
        return ResponseEntity.ok(createdOrder);
    }

    // 2. Get Order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable String orderId) {
        OrderResponseDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    // 3. Get Orders by User ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(@PathVariable String userId) {
        List<OrderResponseDTO> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    // 4. Update Order Item Status
    @PatchMapping("/item/status")
    public ResponseEntity<Void> updateOrderItemStatus(@RequestBody UpdateOrderItemStatusDTO updateDTO) {
        orderService.updateOrderItemStatus(updateDTO);
        return ResponseEntity.noContent().build();
    }

    // 5. Add Review to Order Item
    @PatchMapping("/item/review")
    public ResponseEntity<Void> addReviewToOrderItem(@RequestBody AddReviewDTO reviewDTO) {
        orderService.addReviewToOrderItem(reviewDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(@RequestBody CheckoutRequestDTO checkoutRequestDTO) {
        OrderResponseDTO orderResponseDTO = orderService.checkout(checkoutRequestDTO.getUserId());
        return ResponseEntity.ok(orderResponseDTO);
    }
}
