package com.nus.sellr.order.payment;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentStrategyFactory {

    private final Map<String, PaymentStrategy> strategies = new HashMap<>();

    public PaymentStrategyFactory() {
        strategies.put("Credit Card", new CreditCardPaymentStrategy());
        strategies.put("PayPal", new PaypalPaymentStrategy());
        strategies.put("PayNow", new BankTransferPaymentStrategy());
    }

    public PaymentStrategy getStrategy(String method) {
        PaymentStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new RuntimeException("Unsupported payment method: " + method);
        }
        return strategy;
    }
}
