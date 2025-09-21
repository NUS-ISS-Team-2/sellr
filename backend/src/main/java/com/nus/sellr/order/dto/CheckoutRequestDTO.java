package com.nus.sellr.order.dto;

import java.util.List;

import com.nus.sellr.order.entity.PaymentDetails;
import com.nus.sellr.order.entity.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequestDTO {
    private String userId;
    private Address address;
    private String paymentMethod;
    private PaymentDetails paymentDetails;
    private List<OrderItemDTO> items;
    private double subtotal;
}
