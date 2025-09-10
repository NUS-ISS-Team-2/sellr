package com.nus.sellr.order.service;

import com.nus.sellr.order.dto.*;
import com.nus.sellr.order.entity.Order;
import com.nus.sellr.order.entity.OrderItem;
import com.nus.sellr.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

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
            OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
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
}
