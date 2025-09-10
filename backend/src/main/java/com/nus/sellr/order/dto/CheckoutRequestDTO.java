package com.nus.sellr.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequestDTO {
    private String userId;
    private String shippingAddress; // optional for now
    private String paymentMethod;   // optional for now (e.g. "CASH_ON_DELIVERY")
}
