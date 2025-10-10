package com.nus.sellr.order.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentStrategyFactoryTest {

    private PaymentStrategyFactory factory;

    @BeforeEach
    void setUp() {
        factory = new PaymentStrategyFactory();
    }

    @Test
    void testGetStrategy_creditCard() {
        PaymentStrategy strategy = factory.getStrategy("Credit Card");
        assertNotNull(strategy);
        assertTrue(strategy instanceof CreditCardPaymentStrategy);
    }

    @Test
    void testGetStrategy_paypal() {
        PaymentStrategy strategy = factory.getStrategy("PayPal");
        assertNotNull(strategy);
        assertTrue(strategy instanceof PaypalPaymentStrategy);
    }

    @Test
    void testGetStrategy_payNow() {
        PaymentStrategy strategy = factory.getStrategy("PayNow");
        assertNotNull(strategy);
        assertTrue(strategy instanceof BankTransferPaymentStrategy);
    }

    @Test
    void testGetStrategy_unsupportedMethod() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> factory.getStrategy("Bitcoin"));
        assertEquals("Unsupported payment method: Bitcoin", ex.getMessage());
    }
}
