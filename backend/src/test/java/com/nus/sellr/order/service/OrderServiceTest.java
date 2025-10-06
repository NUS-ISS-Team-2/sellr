package com.nus.sellr.order.service;

import com.nus.sellr.cart.service.CartService;
import com.nus.sellr.order.dto.*;
import com.nus.sellr.order.entity.Order;
import com.nus.sellr.order.entity.OrderItem;
import com.nus.sellr.order.entity.OrderStatus;
import com.nus.sellr.order.entity.PaymentDetails;
import com.nus.sellr.order.repository.OrderRepository;
import com.nus.sellr.product.dto.ProductResponse;
import com.nus.sellr.product.entity.Product;
import com.nus.sellr.product.service.ProductService;
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

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckout_withCreditCard_success() {

        CheckoutRequestDTO request = createCheckoutRequest("Credit Card");
        request.setPaymentDetails(new PaymentDetails());
        request.getPaymentDetails().setCardNumber("1234");
        request.getPaymentDetails().setCardName("John Doe");
        request.getPaymentDetails().setExpiry("12/25");
        request.getPaymentDetails().setCvv("123");

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

        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.checkout(request));
        assertEquals("Invalid credit card details", ex.getMessage());
    }

    // ----------------- PayPal -----------------
    @Test
    void testCheckout_withPayPal_success() {
        CheckoutRequestDTO request = createCheckoutRequest("PayPal");
        request.setPaymentDetails(new PaymentDetails());
        request.getPaymentDetails().setPaypalEmail("test@paypal.com");

        mockSaveAndProduct();

        OrderResponseDTO response = orderService.checkout(request);

        assertNotNull(response);
        verify(cartService).clearCart("user1");
    }

    @Test
    void testCheckout_withPayPal_invalidDetails() {
        CheckoutRequestDTO request = createCheckoutRequest("PayPal");
        request.setPaymentDetails(new PaymentDetails()); // missing PayPal email

        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.checkout(request));
        assertEquals("Invalid PayPal email", ex.getMessage());
    }

    // ----------------- Bank Transfer -----------------
    @Test
    void testCheckout_withBankTransfer_success() {
        CheckoutRequestDTO request = createCheckoutRequest("Bank Transfer");
        PaymentDetails pd = new PaymentDetails();
        pd.setBankName("DBS");
        pd.setAccountNumber("123456");
        pd.setAccountHolder("John Doe");
        request.setPaymentDetails(pd);

        mockSaveAndProduct();

        OrderResponseDTO response = orderService.checkout(request);

        assertNotNull(response);
        verify(cartService).clearCart("user1");
    }

    @Test
    void testCheckout_withBankTransfer_invalidDetails() {
        CheckoutRequestDTO request = createCheckoutRequest("Bank Transfer");
        request.setPaymentDetails(new PaymentDetails()); // missing bank details

        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.checkout(request));
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
        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setUserId("user1");

        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId("prod1");
        itemDTO.setQuantity(2);
        itemDTO.setShippingFee(BigDecimal.valueOf(5));
        dto.setItems(Collections.singletonList(itemDTO));

        // Mock save
        Order savedOrder = new Order("user1");
        savedOrder.setId("order123");
        savedOrder.setItems(Collections.singletonList(
                new OrderItem("prod1", 2, BigDecimal.valueOf(5), null, null)
        ));
        savedOrder.setOrderPrice(BigDecimal.valueOf(5));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Mock productService
        when(productService.getProductById("prod1"))
                .thenReturn(new ProductResponse(
                        "prod1", "Product 1", "Some description", 100.0,
                        "url", "Category A", 10, "seller1"
                ));

        OrderResponseDTO response = orderService.createOrder(dto);

        assertNotNull(response);
        assertEquals("order123", response.getOrderId());
        assertEquals("user1", response.getUserId());
        assertEquals(BigDecimal.valueOf(5), response.getOrderPrice());
        assertEquals(1, response.getItems().size());

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

    // ----------------- getOrdersByUserId -----------------
    @Test
    void testGetOrdersByUserId_incompleteStatus() {
        Order order = new Order("user1");
        order.setId("order123");

        OrderItem item1 = new OrderItem();
        item1.setProductId("prod1");  // <-- must set productId
        item1.setStatus(OrderStatus.PENDING);
        order.setItems(Collections.singletonList(item1));

        when(orderRepository.findByUserId("user1")).thenReturn(Collections.singletonList(order));

        // Mock productService
        when(productService.getProductById("prod1"))
                .thenReturn(new ProductResponse(
                        "prod1", "Product 1", "Some description", 100.0,
                        "url", "Category A", 10, "seller1"
                ));

        List<OrderResponseDTO> responses = orderService.getOrdersByUserId("user1");

        assertEquals(1, responses.size());
        assertEquals(OrderStatus.INCOMPLETE, order.getOverallStatus());

        verify(orderRepository, never()).save(order); // incomplete orders not saved
    }


    @Test
    void testGetOrdersByUserId_completedStatus() {
        Order order = new Order("user1");
        order.setId("order123");

        OrderItem item1 = new OrderItem();
        item1.setProductId("prod1");  // <-- must set productId
        item1.setStatus(OrderStatus.DELIVERED);
        order.setItems(Collections.singletonList(item1));

        when(orderRepository.findByUserId("user1")).thenReturn(Collections.singletonList(order));

        // Mock productService
        when(productService.getProductById("prod1"))
                .thenReturn(new ProductResponse(
                        "prod1", "Product 1", "Some description", 100.0,
                        "url", "Category A", 10, "seller1"
                ));

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

    @Test
    void testGetOrdersForSeller_normalFlow() {
        // Prepare two orders, only some items belong to seller1
        OrderItem item1 = new OrderItem();
        item1.setProductId("prod1");
        item1.setSellerId("seller1");

        OrderItem item2 = new OrderItem();
        item2.setProductId("prod2");
        item2.setSellerId("seller2");

        Order order = new Order("user1");
        order.setId("order123");
        order.setItems(Arrays.asList(item1, item2));

        when(orderRepository.findOrdersBySellerId("seller1")).thenReturn(Collections.singletonList(order));

        // Mock productService for toResponseDTO
        when(productService.getProductById("prod1")).thenReturn(
                new ProductResponse("prod1", "Product 1", "Desc", 100, "url", "Category A", 10, "seller1")
        );

        List<OrderResponseDTO> results = orderService.getOrdersForSeller("seller1");

        assertEquals(1, results.size());
        OrderResponseDTO dto = results.get(0);
        // Only seller1 items should be kept
        assertEquals(1, dto.getItems().size());
        assertEquals("prod1", dto.getItems().get(0).getProductId());
    }
}
