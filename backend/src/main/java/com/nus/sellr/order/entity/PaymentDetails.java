package com.nus.sellr.order.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDetails {
    // For Credit Card
    private String cardNumber;
    private String cardName;
    private String expiry;
    private String cvv;

    // For PayPal
    private String paypalEmail;

    // For Bank Transfer
    private String bankName;
    private String accountNumber;
    private String accountHolder;
}