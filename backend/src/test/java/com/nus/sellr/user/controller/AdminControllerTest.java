package com.nus.sellr.user.controller;

import com.nus.sellr.user.service.AdminUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminUserControllerTest {

    @Mock
    private AdminUserService adminUserService;

    @InjectMocks
    private AdminUserController adminUserController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------------- Disable User -----------------
    @Test
    void testDisableUser_success() {
        doNothing().when(adminUserService).disableUser("user1");

        ResponseEntity<?> response = adminUserController.disableUser("user1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User disabled", response.getBody());
        verify(adminUserService, times(1)).disableUser("user1");
    }

    @Test
    void testDisableUser_notFound() {
        doThrow(new RuntimeException("User not found")).when(adminUserService).disableUser("userX");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminUserController.disableUser("userX"));

        assertEquals("User not found", ex.getMessage());
        verify(adminUserService, times(1)).disableUser("userX");
    }

    // ----------------- Enable User -----------------
    @Test
    void testEnableUser_success() {
        doNothing().when(adminUserService).enableUser("user1");

        ResponseEntity<?> response = adminUserController.enableUser("user1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User enabled", response.getBody());
        verify(adminUserService, times(1)).enableUser("user1");
    }

    @Test
    void testEnableUser_notFound() {
        doThrow(new RuntimeException("User not found")).when(adminUserService).enableUser("userX");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminUserController.enableUser("userX"));

        assertEquals("User not found", ex.getMessage());
        verify(adminUserService, times(1)).enableUser("userX");
    }
}
