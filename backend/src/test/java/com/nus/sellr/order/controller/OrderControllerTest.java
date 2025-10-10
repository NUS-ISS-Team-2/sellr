package com.nus.sellr.order.controller;

import com.nus.sellr.order.dto.*;
import com.nus.sellr.order.entity.OrderStatus;
import com.nus.sellr.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        // populate DTO as needed
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setOrderId("order123");

        when(orderService.createOrder(createOrderDTO)).thenReturn(responseDTO);

        ResponseEntity<OrderResponseDTO> response = orderController.createOrder(createOrderDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("order123", response.getBody().getOrderId());

        verify(orderService, times(1)).createOrder(createOrderDTO);
    }

    @Test
    void testGetOrderById() {
        String orderId = "order123";
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setOrderId(orderId);

        when(orderService.getOrderById(orderId)).thenReturn(responseDTO);

        ResponseEntity<OrderResponseDTO> response = orderController.getOrderById(orderId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(orderId, response.getBody().getOrderId());

        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void testGetOrdersByUserId() {
        String userId = "user123";
        OrderResponseDTO order1 = new OrderResponseDTO();
        order1.setOrderId("order1");
        OrderResponseDTO order2 = new OrderResponseDTO();
        order2.setOrderId("order2");
        List<OrderResponseDTO> orders = Arrays.asList(order1, order2);

        when(orderService.getOrdersByUserId(userId)).thenReturn(orders);

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getOrdersByUserId(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("order1", response.getBody().get(0).getOrderId());

        verify(orderService, times(1)).getOrdersByUserId(userId);
    }

    @Test
    void testAddReviewToOrderItem() {
        AddReviewDTO reviewDTO = new AddReviewDTO();
        // populate DTO if needed

        doNothing().when(orderService).addReviewToOrderItem(reviewDTO);

        ResponseEntity<Void> response = orderController.addReviewToOrderItem(reviewDTO);

        assertEquals(204, response.getStatusCodeValue()); // no content
        verify(orderService, times(1)).addReviewToOrderItem(reviewDTO);
    }

    @Test
    void testCheckout() {
        CheckoutRequestDTO request = new CheckoutRequestDTO();
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setOrderId("order1");

        when(orderService.checkout(request)).thenReturn(responseDTO);

        ResponseEntity<OrderResponseDTO> response = orderController.checkout(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("order1", response.getBody().getOrderId());
        verify(orderService, times(1)).checkout(request);
    }

    @Test
    void testGetOrdersForSeller_withOrders() {
        List<OrderResponseDTO> orders = List.of(new OrderResponseDTO());
        when(orderService.getOrdersForSeller("seller1")).thenReturn(orders);

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getOrdersForSeller("seller1");

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void testGetOrdersForSeller_noOrders() {
        when(orderService.getOrdersForSeller("seller1")).thenReturn(List.of());

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getOrdersForSeller("seller1");

        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testUpdateItemStatus() {
        UpdateOrderItemStatusDTO request = new UpdateOrderItemStatusDTO();
        request.setOrderId("order1");
        request.setProductId("prod1");

        doNothing().when(orderService).updateOrderItemStatusAsSeller(
                anyString(), anyString(), anyString(), any(), any()
        );

        orderController.updateItemStatus(request);

        verify(orderService, times(1)).updateOrderItemStatusAsSeller(
                request.getOrderId(),
                request.getProductId(),
                request.getSellerId(),
                request.getStatus(),
                request.getDeliveryDate()
        );
    }

    @Test
    void testUpdateItemStatusAsBuyer() {
        // Arrange
        UpdateOrderItemStatusDTO request = new UpdateOrderItemStatusDTO();
        request.setOrderId("order1");
        request.setProductId("prod1");
        request.setStatus(OrderStatus.DELIVERED); // example enum

        // Mock the service to do nothing (since it's void)
        doNothing().when(orderService).updateOrderItemStatusAsBuyer(
                anyString(), anyString(), any(OrderStatus.class)
        );

        // Act
        orderController.updateItemStatusAsBuyer(request);

        // Assert
        verify(orderService, times(1)).updateOrderItemStatusAsBuyer(
                "order1",
                "prod1",
                OrderStatus.DELIVERED
        );
    }

    @Test
    void testRaiseDispute_success() {
        DisputeRequestDTO dto = new DisputeRequestDTO();
        dto.setOrderId("order1");
        dto.setProductId("prod1");
        dto.setReason("Damaged");
        dto.setDescription("Item arrived broken");

        doNothing().when(orderService).raiseDispute(anyString(), anyString(), anyString(), anyString());

        ResponseEntity<?> response = orderController.raiseDispute(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Dispute raised successfully.", response.getBody());
        verify(orderService, times(1)).raiseDispute("order1", "prod1", "Damaged", "Item arrived broken");
    }

    @Test
    void testRaiseDispute_runtimeException() {
        DisputeRequestDTO dto = new DisputeRequestDTO();
        dto.setOrderId("order1");
        dto.setProductId("prod1");

        // Use any() instead of anyString() to match null
        doThrow(new RuntimeException("Order not found"))
                .when(orderService).raiseDispute(any(), any(), any(), any());

        ResponseEntity<?> response = orderController.raiseDispute(dto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Order not found", response.getBody());
        verify(orderService, times(1)).raiseDispute(any(), any(), any(), any());
    }


    // ----------------- getAllOrders -----------------
    @Test
    void testGetAllOrders_nonEmpty() {
        OrderResponseDTO order = new OrderResponseDTO();
        when(orderService.getAllOrders()).thenReturn(Collections.singletonList(order));

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getAllOrders();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void testGetAllOrders_empty() {
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getAllOrders();

        assertEquals(204, response.getStatusCodeValue()); // No content
        assertNull(response.getBody());
        verify(orderService, times(1)).getAllOrders();
    }

    // ----------------- resolveDispute -----------------
    @Test
    void testResolveDispute_success() {
        ResolveDisputeDTO dto = new ResolveDisputeDTO();
        dto.setOrderId("order1");
        dto.setProductId("prod1");

        doNothing().when(orderService).resolveDispute(anyString(), anyString());

        ResponseEntity<?> response = orderController.resolveDispute(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Dispute resolved successfully.", response.getBody());
        verify(orderService, times(1)).resolveDispute("order1", "prod1");
    }

    @Test
    void testResolveDispute_runtimeException() {
        ResolveDisputeDTO dto = new ResolveDisputeDTO();
        dto.setOrderId("order1");
        dto.setProductId("prod1");

        doThrow(new RuntimeException("Order not found")).when(orderService).resolveDispute(anyString(), anyString());

        ResponseEntity<?> response = orderController.resolveDispute(dto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Order not found", response.getBody());
        verify(orderService, times(1)).resolveDispute("order1", "prod1");
    }

}
