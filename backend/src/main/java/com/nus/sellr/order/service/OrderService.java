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
import com.nus.sellr.order.entity.PaymentDetails;

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

    public OrderResponseDTO checkout(CheckoutRequestDTO request) {
        // Payment verification
        if ("Credit Card".equals(request.getPaymentMethod())) {
            PaymentDetails pd = request.getPaymentDetails();
            if (pd.getCardNumber() == null || pd.getCardName() == null
                || pd.getExpiry() == null || pd.getCvv() == null) {
                throw new RuntimeException("Invalid credit card details");
            }
        } else if ("PayPal".equals(request.getPaymentMethod())) {
            if (request.getPaymentDetails().getPaypalEmail() == null) {
                throw new RuntimeException("Invalid PayPal email");
            }
        } else if ("Bank Transfer".equals(request.getPaymentMethod())) {
            PaymentDetails pd = request.getPaymentDetails();
            if (pd.getBankName() == null || pd.getAccountNumber() == null
                || pd.getAccountHolder() == null) {
                throw new RuntimeException("Invalid bank transfer details");
            }
        }

        Order order = new Order(request.getUserId());
        order.setItems(request.getItems().stream()
            .map(dto -> {
                OrderItem item = new OrderItem();
                item.setProductId(dto.getProductId());
                item.setQuantity(dto.getQuantity());
                item.setShippingFee(dto.getShippingFee());
                item.setStatus(OrderStatus.PENDING);
                item.setImageUrl(dto.getImageUrl());
                return item;
            }).collect(Collectors.toList())
        );
        order.setOrderPrice(BigDecimal.valueOf(request.getSubtotal()));
        order.setAddress(request.getAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentDetails(request.getPaymentDetails());
        order.setOverallStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(request.getUserId());

        return toResponseDTO(savedOrder);
    }

    public OrderResponseDTO createOrder(CreateOrderDTO createOrderDTO) {
        Order order = new Order(createOrderDTO.getUserId());

        // Convert DTO items to entities (only store productId, quantity, shippingFee)
        List<OrderItem> items = createOrderDTO.getItems().stream()
                .map(dto -> new OrderItem(dto.getProductId(), dto.getQuantity(), dto.getShippingFee(), null))
                .collect(Collectors.toList());

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
                .map(OrderItem::getShippingFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ----------------------
    // Convert Order -> OrderResponseDTO
    private OrderResponseDTO toResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setOrderPrice(order.getOrderPrice());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setOverallStatus(order.getOverallStatus());
        dto.setAddress(order.getAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentDetails(order.getPaymentDetails());

        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(this::toOrderItemDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }

    // Convert OrderItem -> OrderItemDTO dynamically
    private OrderItemDTO toOrderItemDTO(OrderItem item) {
        ProductResponse product = productService.getProductById(item.getProductId());

        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(item.getProductId());
        dto.setProductName(product.getName());
        dto.setImageUrl(product.getImageUrl());
        dto.setQuantity(item.getQuantity());
        dto.setShippingFee(item.getShippingFee());
        dto.setStatus(item.getStatus());
        dto.setDeliveryDate(item.getDeliveryDate());
        dto.setRating(item.getRating());
        dto.setReview(item.getReview());

        return dto;
    }

    public OrderResponseDTO checkout(String userId) {
        CartDTO cart = cartService.getCartByUserId(userId);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(this::createOrderItemFromCartItem)
                .collect(Collectors.toList());

        BigDecimal totalPrice = orderItems.stream()
                .map(this::getProductPriceFromItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order(userId);
        order.setItems(orderItems);
        order.setOrderPrice(totalPrice);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(userId);

        return toResponseDTO(savedOrder);
    }

    private OrderItem createOrderItemFromCartItem(CartItemDTO cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(cartItem.getProductId());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setShippingFee(BigDecimal.valueOf(0));
        orderItem.setStatus(OrderStatus.PENDING);
        return orderItem;
    }

    private BigDecimal getProductPriceFromItem(OrderItem item) {
        ProductResponse product = productService.getProductById(item.getProductId());
        return BigDecimal.valueOf(product.getPrice());
    }

}
