package com.nus.sellr.order.service;

import com.nus.sellr.cart.service.CartService;
import com.nus.sellr.order.dto.*;
import com.nus.sellr.order.entity.Order;
import com.nus.sellr.order.entity.OrderItem;
import com.nus.sellr.order.entity.OrderStatus;
import com.nus.sellr.order.payment.PaymentStrategy;
import com.nus.sellr.order.payment.PaymentStrategyFactory;
import com.nus.sellr.order.repository.OrderRepository;
import com.nus.sellr.product.dto.ProductResponse;
import com.nus.sellr.product.entity.Product;
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
    private final PaymentStrategyFactory paymentStrategyFactory;

    public OrderResponseDTO checkout(CheckoutRequestDTO request) {
        PaymentStrategy paymentStrategy = paymentStrategyFactory.getStrategy(request.getPaymentMethod());
        paymentStrategy.validate(request.getPaymentDetails());
        paymentStrategy.processPayment(request.getPaymentDetails());

        Order order = new Order(request.getUserId());
        order.setItems(request.getItems().stream()
            .map(dto -> {
                OrderItem item = new OrderItem();
                item.setProductId(dto.getProductId());
                item.setQuantity(dto.getQuantity());
                item.setShippingFee(dto.getShippingFee());
                item.setStatus(OrderStatus.PENDING);
                item.setImageUrl(dto.getImageUrl());
                item.setSellerId((dto.getSellerId()));
                item.setPrice(dto.getPrice());
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

        for (OrderItem item : savedOrder.getItems()) {
            Product product = productService.getProductEntityById(item.getProductId());
            int updatedStock = product.getStock() - item.getQuantity();
            product.setStock(Math.max(0, updatedStock));  // prevent negative stock
            productService.saveProduct(product);
        }

        cartService.clearCart(request.getUserId());

        return toResponseDTO(savedOrder);
    }

    public OrderResponseDTO createOrder(CreateOrderDTO createOrderDTO) {
        Order order = new Order(createOrderDTO.getUserId());

        // Convert DTO items to entities (only store productId, quantity, shippingFee)
        List<OrderItem> items = createOrderDTO.getItems().stream()
                .map(dto -> new OrderItem(dto.getProductId(), dto.getQuantity(),
                        dto.getShippingFee(), null, null))
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
                .map(order -> {
                    // Count items by status
                    long pendingCount = order.getItems().stream()
                            .filter(item -> item.getStatus() == OrderStatus.PENDING)
                            .count();

                    long shippedCount = order.getItems().stream()
                            .filter(item -> item.getStatus() == OrderStatus.SHIPPED)
                            .count();

                    long deliveredCount = order.getItems().stream()
                            .filter(item -> item.getStatus() == OrderStatus.DELIVERED)
                            .count();

                    // If all items are delivered, mark overall status as COMPLETED
                    if (pendingCount == 0 && shippedCount == 0 && deliveredCount > 0) {
                        order.setOverallStatus(OrderStatus.COMPLETED);
                        orderRepository.save(order);
                    } else {
                        order.setOverallStatus(OrderStatus.INCOMPLETE);
                    }

                    // You can also store counts in the DTO if needed
                    OrderResponseDTO dto = toResponseDTO(order);

                    return dto;
                })
                .collect(Collectors.toList());
    }


//    public void updateOrderItemStatus(UpdateOrderItemStatusDTO updateDTO) {
//        Order order = orderRepository.findById(updateDTO.getOrderId())
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        order.getItems().stream()
//                .filter(i -> i.getProductId().equals(updateDTO.getProductId()))
//                .findFirst()
//                .ifPresent(item -> {
//                    item.setStatus(updateDTO.getStatus());
//                    item.setDeliveryDate(updateDTO.getDeliveryDate());
//                });
//
//        orderRepository.save(order);
//    }

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
        dto.setSellerId(item.getSellerId());
        dto.setPrice(item.getPrice());
        return dto;
    }

//    private OrderItem createOrderItemFromCartItem(CartItemDTO cartItem) {
//        OrderItem orderItem = new OrderItem();
//        orderItem.setProductId(cartItem.getProductId());
//        orderItem.setQuantity(cartItem.getQuantity());
//        orderItem.setShippingFee(BigDecimal.valueOf(0));
//        orderItem.setStatus(OrderStatus.PENDING);
//        orderItem.setSellerId(cartItem.getSellerId());
//        return orderItem;
//    }
//
//    private BigDecimal getProductPriceFromItem(OrderItem item) {
//        ProductResponse product = productService.getProductById(item.getProductId());
//        return BigDecimal.valueOf(product.getPrice());
//    }

    // Fetch all orders for a seller, only including their items
    public List<OrderResponseDTO> getOrdersForSeller(String sellerId) {

        List<Order> orders = orderRepository.findOrdersBySellerId(sellerId);
        System.out.println("Orders found: " + orders.size());
        orders.forEach(System.out::println);

        return orders.stream()
                .map(order -> {
                    List<OrderItem> sellerItems = order.getItems().stream()
                            .filter(item -> sellerId.equals(item.getSellerId()))
                            .collect(Collectors.toList());

                    if (sellerItems.isEmpty()) {
                        return null; // safety check
                    }
                    order.setItems(sellerItems); // only keep seller's items
                    return toResponseDTO(order);
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    public void updateOrderItemStatusAsSeller(String orderId, String productId, String sellerId, OrderStatus status,
                                              LocalDateTime deliveryDate) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Only update if the item belongs to the seller
        order.getItems().stream()
                .filter(item -> productId.equals(item.getProductId()) && sellerId.equals(item.getSellerId()))
                .findFirst()
                .ifPresent(item -> {
                    item.setStatus(status);
                    item.setDeliveryDate(deliveryDate); // update delivery date
                });

        // Check if all items are now shipped
        boolean allShipped = order.getItems().stream()
                .allMatch(item -> item.getStatus() == OrderStatus.SHIPPED);

        if (allShipped) {
            order.setOverallStatus(OrderStatus.SHIPPED);
        }

        orderRepository.save(order);
    }

    public void updateOrderItemStatusAsBuyer(String orderId, String productId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Find the item
        order.getItems().stream()
                .filter(item -> productId.equals(item.getProductId()))
                .findFirst()
                .ifPresent(item -> {
                    // Only allow DELIVERED if current status is SHIPPED
                    if (item.getStatus() != OrderStatus.SHIPPED) {
                        throw new RuntimeException("Item must be shipped before it can be delivered.");
                    }

                    item.setStatus(status);
                    item.setDeliveryDate(LocalDateTime.now());
                });

        // If all items are now DELIVERED, update overall order status
        boolean allDelivered = order.getItems().stream()
                .allMatch(item -> item.getStatus() == OrderStatus.DELIVERED);

        if (allDelivered) {
            order.setOverallStatus(OrderStatus.DELIVERED);
        }

        orderRepository.save(order);
    }



}
