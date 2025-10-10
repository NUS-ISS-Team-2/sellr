package com.nus.sellr.order.payment;

import com.nus.sellr.order.entity.PaymentDetails;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentStrategyTest {

    // -------------------- Credit Card --------------------
    @Test
    void testCreditCardPaymentStrategy_valid() {
        CreditCardPaymentStrategy strategy = new CreditCardPaymentStrategy();
        PaymentDetails details = new PaymentDetails();
        details.setCardNumber("4111111111111111");
        details.setCardName("John Doe");
        details.setExpiry("12/25");
        details.setCvv("123");

        assertDoesNotThrow(() -> strategy.validate(details));
    }

    @Test
    void testCreditCardPaymentStrategy_invalid() {
        CreditCardPaymentStrategy strategy = new CreditCardPaymentStrategy();
        PaymentDetails details = new PaymentDetails(); // missing fields

        RuntimeException ex = assertThrows(RuntimeException.class, () -> strategy.validate(details));
        assertEquals("Invalid credit card details", ex.getMessage());
    }

    @Test
    void testCreditCardPaymentStrategy_processPayment() {
        CreditCardPaymentStrategy strategy = new CreditCardPaymentStrategy();
        PaymentDetails details = new PaymentDetails();
        details.setCardNumber("4111111111111111");

        // Just to cover the method
        strategy.processPayment(details);
    }

    // -------------------- PayPal --------------------
    @Test
    void testPaypalPaymentStrategy_valid() {
        PaypalPaymentStrategy strategy = new PaypalPaymentStrategy();
        PaymentDetails details = new PaymentDetails();
        details.setPaypalEmail("test@example.com");

        assertDoesNotThrow(() -> strategy.validate(details));
    }

    @Test
    void testPaypalPaymentStrategy_invalid() {
        PaypalPaymentStrategy strategy = new PaypalPaymentStrategy();
        PaymentDetails details = new PaymentDetails(); // missing email

        RuntimeException ex = assertThrows(RuntimeException.class, () -> strategy.validate(details));
        assertEquals("Invalid PayPal email", ex.getMessage());
    }

    @Test
    void testPaypalPaymentStrategy_processPayment() {
        PaypalPaymentStrategy strategy = new PaypalPaymentStrategy();
        PaymentDetails details = new PaymentDetails();
        details.setPaypalEmail("test@example.com");

        strategy.processPayment(details);
    }

    // -------------------- Bank Transfer / PayNow --------------------
    @Test
    void testBankTransferPaymentStrategy_valid() {
        BankTransferPaymentStrategy strategy = new BankTransferPaymentStrategy();
        PaymentDetails details = new PaymentDetails();
        details.setReferenceNumber("12345678");

        assertDoesNotThrow(() -> strategy.validate(details));
    }

    @Test
    void testBankTransferPaymentStrategy_invalid() {
        BankTransferPaymentStrategy strategy = new BankTransferPaymentStrategy();
        PaymentDetails details = new PaymentDetails(); // missing reference number

        RuntimeException ex = assertThrows(RuntimeException.class, () -> strategy.validate(details));
        assertEquals("Invalid bank transfer details", ex.getMessage());
    }

    @Test
    void testBankTransferPaymentStrategy_processPayment() {
        BankTransferPaymentStrategy strategy = new BankTransferPaymentStrategy();
        PaymentDetails details = new PaymentDetails();
        details.setReferenceNumber("12345678");

        strategy.processPayment(details);
    }
}
