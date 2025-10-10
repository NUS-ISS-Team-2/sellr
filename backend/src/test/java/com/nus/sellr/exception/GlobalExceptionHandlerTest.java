package com.nus.sellr.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    @Test
    void testHandleIllegalArgument() {
        // Arrange
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleIllegalArgument(ex);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Invalid input", response.getBody().get("error"));
    }
}
