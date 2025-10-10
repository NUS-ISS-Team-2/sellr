package com.nus.sellr.order.payment;

import com.nus.sellr.order.entity.PaymentDetails;

public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public void validate(PaymentDetails paymentDetails) {
        if (paymentDetails.getCardNumber() == null ||
                paymentDetails.getCardName() == null ||
                paymentDetails.getExpiry() == null ||
                paymentDetails.getCvv() == null) {
            throw new RuntimeException("Invalid credit card details");
        }
    }

    @Override
    public void processPayment(PaymentDetails paymentDetails) {
        // Mock logic or integrate with payment gateway
        System.out.println("Processing credit card payment for card: " + paymentDetails.getCardNumber());
    }
}
