package com.nus.sellr.order.payment;

import com.nus.sellr.order.entity.PaymentDetails;

public class BankTransferPaymentStrategy implements PaymentStrategy {

    @Override
    public void validate(PaymentDetails paymentDetails) {
        if (paymentDetails.getReferenceNumber() == null) {
            throw new RuntimeException("Invalid bank transfer details");
        }
    }

    @Override
    public void processPayment(PaymentDetails paymentDetails) {
        System.out.println("Processing bank transfer to:");
    }
}
