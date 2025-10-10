package com.nus.sellr.order.payment;

import com.nus.sellr.order.entity.PaymentDetails;

public interface PaymentStrategy {
    void validate(PaymentDetails paymentDetails);
    void processPayment(PaymentDetails paymentDetails);
}
