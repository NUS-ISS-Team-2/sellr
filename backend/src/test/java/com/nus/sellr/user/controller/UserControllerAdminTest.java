package com.nus.sellr.user.controller;

import com.nus.sellr.user.entity.Buyer;
import com.nus.sellr.user.entity.User;
import com.nus.sellr.user.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerAdminTest {

    @Mock
    private UserRepositoryImpl userRepository;

    @InjectMocks
    private UserController userController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new Buyer();
        mockUser.setId("u1");
        mockUser.setDisabled(false);
    }

    @Test
    void testDisableUser_success() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(mockUser));
        doAnswer(invocation -> null).when(userRepository).save(mockUser);

        ResponseEntity<?> response = userController.disableUser("u1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User disabled", response.getBody());
        assertTrue(mockUser.isDisabled());
        verify(userRepository, times(1)).findById("u1");
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testEnableUser_success() {
        mockUser.setDisabled(true);
        when(userRepository.findById("u1")).thenReturn(Optional.of(mockUser));
        doAnswer(invocation -> null).when(userRepository).save(mockUser);

        ResponseEntity<?> response = userController.enableUser("u1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User enabled", response.getBody());
        assertFalse(mockUser.isDisabled());
        verify(userRepository, times(1)).findById("u1");
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testDisableUser_notFound() {
        when(userRepository.findById("u2")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userController.disableUser("u2");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById("u2");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testEnableUser_notFound() {
        when(userRepository.findById("u2")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userController.enableUser("u2");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById("u2");
        verify(userRepository, never()).save(any());
    }
}
