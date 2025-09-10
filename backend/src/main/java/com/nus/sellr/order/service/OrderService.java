package com.nus.sellr.order.service;

import com.nus.sellr.cart.dto.CartDTO;
import com.nus.sellr.cart.dto.CartItemDTO;
import com.nus.sellr.cart.service.CartService;
import com.nus.sellr.order.dto.*;
import com.nus.sellr.order.entity.Order;
import com.nus.sellr.order.entity.OrderItem;
import com.nus.sellr.order.entity.OrderStatus;
import com.nus.sellr.order.repository.OrderRepository;
import com.nus.sellr.product.dto.ProductResponse;
import com.nus.sellr.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;

    public OrderResponseDTO createOrder(CreateOrderDTO createOrderDTO) {
        Order order = new Order(createOrderDTO.getUserId());

        // Convert DTO items to entities
        List<OrderItem> items = createOrderDTO.getItems().stream().map(dto -> {
            OrderItem item = new OrderItem(dto.getProductId(), dto.getQuantity(), dto.getShippingFee());
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);
        order.setOrderPrice(calculateTotal(items));

        Order saved = orderRepository.save(order);
        return toResponseDTO(saved);
    }

    public OrderResponseDTO getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<OrderResponseDTO> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateOrderItemStatus(UpdateOrderItemStatusDTO updateDTO) {
        Order order = orderRepository.findById(updateDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.getItems().stream()
                .filter(i -> i.getProductId().equals(updateDTO.getProductId()))
                .findFirst()
                .ifPresent(item -> {
                    item.setStatus(updateDTO.getStatus());
                    item.setDeliveryDate(updateDTO.getDeliveryDate());
                });

        orderRepository.save(order);
    }

    public void addReviewToOrderItem(AddReviewDTO reviewDTO) {
        Order order = orderRepository.findById(reviewDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.getItems().stream()
                .filter(i -> i.getProductId().equals(reviewDTO.getProductId()))
                .findFirst()
                .ifPresent(item -> {
                    item.setRating(reviewDTO.getRating());
                    item.setReview(reviewDTO.getReview());
                });

        orderRepository.save(order);
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getShippingFee) // add product price if available
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderResponseDTO toResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setOrderPrice(order.getOrderPrice());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setItems(order.getItems().stream().map(i -> {
            OrderItem itemDTO = new OrderItem();
            itemDTO.setProductId(i.getProductId());
            itemDTO.setQuantity(i.getQuantity());
            itemDTO.setShippingFee(i.getShippingFee());
            itemDTO.setDeliveryDate(i.getDeliveryDate());
            itemDTO.setStatus(i.getStatus());
            itemDTO.setRating(i.getRating());
            itemDTO.setReview(i.getReview());
            return itemDTO;
        }).collect(Collectors.toList()));
        return dto;
    }

    public OrderResponseDTO checkout(String userId) {
        // 1. Get cart
        CartDTO cart = cartService.getCartByUserId(userId);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(this::createOrderItemFromCartItem)
                .collect(Collectors.toList());

        // 3. Calculate total price
        BigDecimal totalPrice = orderItems.stream()
                .map(i -> BigDecimal.valueOf(0)
                        .add(getProductPrice(i.getProductId())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Create Order
        Order order = new Order(userId);
        order.setItems(orderItems);
        order.setOrderPrice(BigDecimal.valueOf(totalPrice.doubleValue())); // or keep BigDecimal in entity
        order.setCreatedAt(LocalDateTime.now());

        // 5. Save Order
        Order savedOrder = orderRepository.save(order);

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setOrderId(savedOrder.getId());
        orderResponseDTO.setOrderPrice(savedOrder.getOrderPrice());

        // 6. Clear cart
        cartService.clearCart(userId);

        return orderResponseDTO;
    }

    private OrderItem createOrderItemFromCartItem(CartItemDTO cartItem) {
        ProductResponse product = productService.getProductById(cartItem.getProductId());

        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(product.getId());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setShippingFee(BigDecimal.valueOf(0));
        orderItem.setStatus(OrderStatus.PENDING);
        return orderItem;
    }

    private BigDecimal getProductPrice(String productId) {
        ProductResponse product = productService.getProductById(productId);
        return BigDecimal.valueOf(product.getPrice());
    }

}
