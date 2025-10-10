package com.nus.sellr.order.service;

import com.nus.sellr.cart.service.CartService;
import com.nus.sellr.order.dto.*;
import com.nus.sellr.order.entity.Order;
import com.nus.sellr.order.entity.OrderItem;
import com.nus.sellr.order.entity.OrderStatus;
import com.nus.sellr.order.entity.PaymentDetails;
import com.nus.sellr.order.payment.PaymentStrategy;
import com.nus.sellr.order.payment.PaymentStrategyFactory;
import com.nus.sellr.order.repository.OrderRepository;
import com.nus.sellr.product.dto.ProductResponse;
import com.nus.sellr.product.entity.Product;
import com.nus.sellr.product.service.ProductService;
import com.nus.sellr.user.entity.Seller;
import com.nus.sellr.user.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentStrategyFactory paymentStrategyFactory;

    @Mock
    private PaymentStrategy paymentStrategy;

    @InjectMocks
    private OrderService orderService; // Injects all mocks into constructor
    @Mock
    private SellerRepository sellerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Make the factory return the mock payment strategy
        when(paymentStrategyFactory.getStrategy(anyString())).thenReturn(paymentStrategy);

        // Mock the strategy behavior
        doNothing().when(paymentStrategy).validate(any());
        doNothing().when(paymentStrategy).processPayment(any());
    }

    @Test
    void testCheckout_withCreditCard_success() {
        CheckoutRequestDTO request = createCheckoutRequest("Credit Card");

        PaymentDetails pd = new PaymentDetails();
        pd.setCardNumber("1234");
        pd.setCardName("John Doe");
        pd.setExpiry("12/25");
        pd.setCvv("123");
        request.setPaymentDetails(pd);

        // Mock save and product service
        mockSaveAndProduct();

        OrderResponseDTO response = orderService.checkout(request);

        assertNotNull(response);
        verify(cartService, times(1)).clearCart("user1");
        verify(productService, times(1)).saveProduct(any());
    }

    @Test
    void testCheckout_withCreditCard_invalidDetails() {
        CheckoutRequestDTO request = createCheckoutRequest("Credit Card");
        request.setPaymentDetails(new PaymentDetails()); // missing card details

        // Mock factory to return the mock paymentStrategy
        when(paymentStrategyFactory.getStrategy("Credit Card")).thenReturn(paymentStrategy);

        // Mock strategy to throw exception on invalid details
        doThrow(new RuntimeException("Invalid credit card details"))
                .when(paymentStrategy).validate(any(PaymentDetails.class));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.checkout(request));
        assertEquals("Invalid credit card details", ex.getMessage());
    }


    @Test
    void testCheckout_withPayPal_success() {
        CheckoutRequestDTO request = createCheckoutRequest("PayPal");

        PaymentDetails pd = new PaymentDetails();
        pd.setPaypalEmail("test@paypal.com");
        request.setPaymentDetails(pd);

        // Mock save and product service
        mockSaveAndProduct();

        OrderResponseDTO response = orderService.checkout(request);

        assertNotNull(response);
        verify(cartService, times(1)).clearCart("user1");
    }

    @Test
    void testCheckout_withPayPal_invalidDetails() {
        CheckoutRequestDTO request = createCheckoutRequest("PayPal");
        request.setPaymentDetails(new PaymentDetails()); // missing PayPal email

        // Mock factory to return the mock paymentStrategy
        when(paymentStrategyFactory.getStrategy("PayPal")).thenReturn(paymentStrategy);

        // Mock strategy to throw exception on invalid details
        doThrow(new RuntimeException("Invalid PayPal email"))
                .when(paymentStrategy).validate(any(PaymentDetails.class));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.checkout(request));

        assertEquals("Invalid PayPal email", ex.getMessage());
    }


    @Test
    void testCheckout_withBankTransfer_success() {
        // Use existing helper
        CheckoutRequestDTO request = createCheckoutRequest("Bank Transfer");

        PaymentDetails pd = new PaymentDetails();
        pd.setReferenceNumber("123456"); // valid bank transfer info
        request.setPaymentDetails(pd);

        // Mock repository and product service
        mockSaveAndProduct();

        OrderResponseDTO response = orderService.checkout(request);

        assertNotNull(response);
        verify(cartService).clearCart("user1");
    }

    @Test
    void testCheckout_withBankTransfer_invalidDetails() {
        CheckoutRequestDTO request = createCheckoutRequest("Bank Transfer");
        request.setPaymentDetails(new PaymentDetails()); // missing required bank info

        // Mock factory to return the mock payment strategy
        when(paymentStrategyFactory.getStrategy("Bank Transfer")).thenReturn(paymentStrategy);

        // Mock strategy to throw exception for invalid details
        doThrow(new RuntimeException("Invalid bank transfer details"))
                .when(paymentStrategy).validate(any(PaymentDetails.class));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.checkout(request));

        assertEquals("Invalid bank transfer details", ex.getMessage());
    }



    // ----------------- Helper Methods -----------------
    private CheckoutRequestDTO createCheckoutRequest(String method) {
        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setUserId("user1");
        request.setPaymentMethod(method);
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId("prod1");
        itemDTO.setQuantity(2);
        itemDTO.setPrice(100);
        itemDTO.setShippingFee(BigDecimal.valueOf(10));
        itemDTO.setSellerId("seller1");
        request.setItems(Collections.singletonList(itemDTO));
        request.setSubtotal(120);
        return request;
    }

    private void mockSaveAndProduct() {
        // Mock OrderRepository.save
        Order savedOrder = new Order("user1");
        savedOrder.setId("order123");
        savedOrder.setItems(Collections.singletonList(new OrderItem() {{
            setProductId("prod1");
            setQuantity(2);
            setPrice(100);
            setShippingFee(BigDecimal.valueOf(10));
            setStatus(OrderStatus.PENDING);
            setSellerId("seller1");
        }}));
        savedOrder.setOrderPrice(BigDecimal.valueOf(120));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Mock ProductService
        Product product = new Product();
        product.setStock(10);
        when(productService.getProductEntityById("prod1")).thenReturn(product);
        when(productService.getProductById("prod1")).thenReturn(
                new com.nus.sellr.product.dto.ProductResponse(
                        "prod1", "Product 1", "Desc", 100, "url", "Category", 10, "seller1"
                )
        );
    }

    @Test
    void testUpdateOrderItemStatusAsSeller_updatesStatusCorrectly() {
        Order order = new Order("user1");
        OrderItem item = new OrderItem();
        item.setProductId("prod1");
        item.setSellerId("seller1");
        item.setStatus(OrderStatus.PENDING);
        order.setItems(Collections.singletonList(item));
        order.setId("order123");

        when(orderRepository.findById("order123")).thenReturn(Optional.of(order));

        orderService.updateOrderItemStatusAsSeller("order123", "prod1", "seller1", OrderStatus.SHIPPED, LocalDateTime.now());

        assertEquals(OrderStatus.SHIPPED, order.getItems().get(0).getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testUpdateOrderItemStatusAsBuyer_throwsIfNotShipped() {
        Order order = new Order("user1");
        OrderItem item = new OrderItem();
        item.setProductId("prod1");
        item.setStatus(OrderStatus.PENDING);
        order.setItems(Collections.singletonList(item));
        order.setId("order123");

        when(orderRepository.findById("order123")).thenReturn(Optional.of(order));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                orderService.updateOrderItemStatusAsBuyer("order123", "prod1", OrderStatus.DELIVERED)
        );

        assertEquals("Item must be shipped before it can be delivered.", exception.getMessage());
    }

    // ----------------- createOrder -----------------
    @Test
    void testCreateOrder_success() {
        // ------------------ Input DTO ------------------
        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setUserId("user1");

        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId("prod1");
        itemDTO.setQuantity(2);
        itemDTO.setShippingFee(BigDecimal.valueOf(5));
        itemDTO.setSellerId("seller1"); // Important for sellerName mapping
        dto.setItems(Collections.singletonList(itemDTO));

        // ------------------ Mock saved order ------------------
        Order savedOrder = new Order("user1");
        savedOrder.setId("order123");
        savedOrder.setItems(Collections.singletonList(
                new OrderItem("prod1", 2, BigDecimal.valueOf(5), null, "seller1")
        ));
        savedOrder.setOrderPrice(BigDecimal.valueOf(5));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // ------------------ Mock product service ------------------
        when(productService.getProductById("prod1"))
                .thenReturn(new ProductResponse(
                        "prod1", "Product 1", "Some description", 100.0,
                        "url", "Category A", 10, "seller1"
                ));

        // ------------------ Mock seller repository ------------------
        Seller mockSeller = new Seller();
        mockSeller.setId("seller1");
        mockSeller.setUsername("Seller One");
        when(sellerRepository.findById("seller1")).thenReturn(Optional.of(mockSeller));

        // ------------------ Call service ------------------
        OrderResponseDTO response = orderService.createOrder(dto);

        // ------------------ Assertions ------------------
        assertNotNull(response);
        assertEquals("order123", response.getOrderId());
        assertEquals("user1", response.getUserId());
        assertEquals(BigDecimal.valueOf(5), response.getOrderPrice());
        assertEquals(1, response.getItems().size());

        OrderItemDTO itemResponse = response.getItems().get(0);
        assertEquals("prod1", itemResponse.getProductId());
        assertEquals("Product 1", itemResponse.getProductName());
        assertEquals("Seller One", itemResponse.getSellerName()); // new field

        // Dispute-related fields
        assertFalse(itemResponse.isDisputeRaised());
        assertNull(itemResponse.getDisputeReason());
        assertNull(itemResponse.getDisputeDescription());
        assertNull(itemResponse.getDisputeRaisedAt());

        verify(orderRepository, times(1)).save(any(Order.class));
    }



    // ----------------- getOrderById -----------------
    @Test
    void testGetOrderById_found() {
        Order order = new Order("user1");
        order.setId("order123");
        when(orderRepository.findById("order123")).thenReturn(Optional.of(order));

        OrderResponseDTO response = orderService.getOrderById("order123");

        assertNotNull(response);
        assertEquals("order123", response.getOrderId());
    }

    @Test
    void testGetOrderById_notFound() {
        when(orderRepository.findById("order123")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                orderService.getOrderById("order123"));
        assertEquals("Order not found", ex.getMessage());
    }

    // Helper to create an order with one item
    private Order createOrderWithItem(String userId, String productId, OrderStatus itemStatus) {
        Order order = new Order(userId);
        order.setId("order123");

        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setStatus(itemStatus);

        order.setItems(Collections.singletonList(item));
        return order;
    }

    // Helper to mock productService
    private void mockProductService(String productId) {
        when(productService.getProductById(productId))
                .thenReturn(new ProductResponse(
                        productId, "Product 1", "Some description", 100.0,
                        "url", "Category A", 10, "seller1"
                ));
    }

    @Test
    void testGetOrdersByUserId_incompleteStatus() {
        Order order = createOrderWithItem("user1", "prod1", OrderStatus.PENDING);
        when(orderRepository.findByUserId("user1")).thenReturn(Collections.singletonList(order));
        mockProductService("prod1");

        List<OrderResponseDTO> responses = orderService.getOrdersByUserId("user1");

        assertEquals(1, responses.size());
        assertEquals(OrderStatus.INCOMPLETE, order.getOverallStatus());

        verify(orderRepository, never()).save(order); // incomplete orders not saved
    }

    @Test
    void testGetOrdersByUserId_completedStatus() {
        Order order = createOrderWithItem("user1", "prod1", OrderStatus.DELIVERED);
        when(orderRepository.findByUserId("user1")).thenReturn(Collections.singletonList(order));
        mockProductService("prod1");

        List<OrderResponseDTO> responses = orderService.getOrdersByUserId("user1");

        assertEquals(1, responses.size());
        assertEquals(OrderStatus.COMPLETED, order.getOverallStatus());

        verify(orderRepository, times(1)).save(order); // COMPLETED orders saved
    }


    @Test
    void testUpdateOrderItemStatusAsBuyer_allDelivered() {
        // Prepare order with one SHIPPED item
        OrderItem item = new OrderItem();
        item.setProductId("prod1");
        item.setStatus(OrderStatus.SHIPPED);

        Order order = new Order("user1");
        order.setId("order123");
        order.setItems(Collections.singletonList(item));

        when(orderRepository.findById("order123")).thenReturn(Optional.of(order));

        // Call service
        orderService.updateOrderItemStatusAsBuyer("order123", "prod1", OrderStatus.DELIVERED);

        // Verify the item status updated
        assertEquals(OrderStatus.DELIVERED, item.getStatus());
        assertNotNull(item.getDeliveryDate());

        // Verify overall order status updated
        assertEquals(OrderStatus.DELIVERED, order.getOverallStatus());

        verify(orderRepository, times(1)).save(order);
    }

    // Helper to create an OrderItem
    private OrderItem createOrderItem(String productId, String sellerId) {
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setSellerId(sellerId);
        return item;
    }

    // Helper to create an Order
    private Order createOrder(String userId, String orderId, List<OrderItem> items) {
        Order order = new Order(userId);
        order.setId(orderId);
        order.setItems(items);
        return order;
    }

    @Test
    void testGetOrdersForSeller_normalFlow() {
        // Prepare items and order
        OrderItem item1 = createOrderItem("prod1", "seller1");
        OrderItem item2 = createOrderItem("prod2", "seller2");
        Order order = createOrder("user1", "order123", Arrays.asList(item1, item2));

        when(orderRepository.findOrdersBySellerId("seller1")).thenReturn(Collections.singletonList(order));

        // Mock productService for toResponseDTO
        when(productService.getProductById("prod1")).thenReturn(
                new ProductResponse("prod1", "Product 1", "Desc", 100, "url", "Category A", 10, "seller1")
        );

        List<OrderResponseDTO> results = orderService.getOrdersForSeller("seller1");

        assertEquals(1, results.size());
        OrderResponseDTO dto = results.get(0);

        // Only seller1 items should be included
        assertEquals(1, dto.getItems().size());
        assertEquals("prod1", dto.getItems().get(0).getProductId());
    }

    @Test
    void testRaiseDispute_success() {
        OrderItem item = new OrderItem();
        item.setProductId("prod1");
        item.setStatus(OrderStatus.DELIVERED);

        Order order = createOrder("user1", "order123", Collections.singletonList(item));

        when(orderRepository.findById("order123")).thenReturn(Optional.of(order));

        orderService.raiseDispute("order123", "prod1", "Damaged", "Broken on arrival");

        assertEquals(OrderStatus.DISPUTING, item.getStatus());
        assertTrue(item.isDisputeRaised());
        assertEquals("Damaged", item.getDisputeReason());
        assertEquals("Broken on arrival", item.getDisputeDescription());
        assertNotNull(item.getDisputeRaisedAt());

        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testRaiseDispute_invalidItemStatus() {
        OrderItem item = new OrderItem();
        item.setProductId("prod1");
        item.setStatus(OrderStatus.PENDING);

        Order order = createOrder("user1", "order123", Collections.singletonList(item));
        when(orderRepository.findById("order123")).thenReturn(Optional.of(order));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                orderService.raiseDispute("order123", "prod1", "Damaged", "Broken on arrival"));

        assertEquals("Dispute can only be raised for shipped or delivered items.", ex.getMessage());
        verify(orderRepository, never()).save(order);
    }

    @Test
    void testRaiseDispute_orderNotFound() {
        when(orderRepository.findById("order123")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                orderService.raiseDispute("order123", "prod1", "Damaged", "Broken on arrival"));

        assertEquals("Order not found", ex.getMessage());
    }


}
