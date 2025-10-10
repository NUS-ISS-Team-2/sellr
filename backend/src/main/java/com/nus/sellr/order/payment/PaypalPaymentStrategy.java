package com.nus.sellr.order.payment;

import com.nus.sellr.order.entity.PaymentDetails;

public class PaypalPaymentStrategy implements PaymentStrategy {

    @Override
    public void validate(PaymentDetails paymentDetails) {
        if (paymentDetails.getPaypalEmail() == null) {
            throw new RuntimeException("Invalid PayPal email");
        }
    }

    @Override
    public void processPayment(PaymentDetails paymentDetails) {
        System.out.println("Processing PayPal payment for email: " + paymentDetails.getPaypalEmail());
    }
}
