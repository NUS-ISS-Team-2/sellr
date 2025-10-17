package com.nus.sellr.order.controller;

import com.nus.sellr.order.dto.*;
import com.nus.sellr.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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


    // 5. Add Review to Order Item
    @PatchMapping("/item/review")
    public ResponseEntity<Void> addReviewToOrderItem(@RequestBody AddReviewDTO reviewDTO) {
        orderService.addReviewToOrderItem(reviewDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDTO> checkout(@RequestBody CheckoutRequestDTO checkoutRequestDTO) {
        OrderResponseDTO orderResponseDTO = orderService.checkout(checkoutRequestDTO);
        return ResponseEntity.ok(orderResponseDTO);
    }

    @GetMapping("/seller")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersForSeller(@RequestParam String sellerId) {
        List<OrderResponseDTO> orders = orderService.getOrdersForSeller(sellerId);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(orders); // 200 OK with list
    }

    // Seller updates status of their item
    @PutMapping("/seller/status")
    public void updateItemStatus(@RequestBody UpdateOrderItemStatusDTO request) {
        orderService.updateOrderItemStatusAsSeller(
                request.getOrderId(),
                request.getProductId(),
                request.getSellerId(),
                request.getStatus(),
                request.getDeliveryDate()
        );
    }

    @PutMapping("/buyer/status")
    public void updateItemStatusAsBuyer(@RequestBody UpdateOrderItemStatusDTO request) {
        orderService.updateOrderItemStatusAsBuyer(
                request.getOrderId(),
                request.getProductId(),
                request.getStatus()
        );
    }

    @PostMapping("/dispute")
    public ResponseEntity<?> raiseDispute(@RequestBody DisputeRequestDTO request){
        try {
            orderService.raiseDispute(
                    request.getOrderId(),
                    request.getProductId(),
                    request.getReason(),
                    request.getDescription()
            );
            return ResponseEntity.ok("Dispute raised successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders();

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(orders); // 200 OK with list
    }

    // PUT /api/orders/item/resolve
    @PutMapping("/item/resolve")
    public ResponseEntity<?> resolveDispute(@RequestBody ResolveDisputeDTO request) {
        try {
            orderService.resolveDispute(request.getOrderId(), request.getProductId());
            return ResponseEntity.ok("Dispute resolved successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        }
    }
}
