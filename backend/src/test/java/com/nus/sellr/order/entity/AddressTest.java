package com.nus.sellr.order.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void testGettersAndSetters() {
        Address address = new Address();

        address.setFullName("John Doe");
        address.setStreet("123 Main St");
        address.setCity("Singapore");
        address.setStateZipCountry("Singapore 123456");

        assertEquals("John Doe", address.getFullName());
        assertEquals("123 Main St", address.getStreet());
        assertEquals("Singapore", address.getCity());
        assertEquals("Singapore 123456", address.getStateZipCountry());
    }
}
