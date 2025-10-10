package com.nus.sellr.user.service;

import com.nus.sellr.user.entity.Buyer;
import com.nus.sellr.user.entity.User;
import com.nus.sellr.user.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminUserServiceTest {

    @Mock
    private UserRepositoryImpl userRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new Buyer();
        mockUser.setId("user1");
        mockUser.setDisabled(false);
    }

    // ----------------- disableUser -----------------
    @Test
    void testDisableUser_success() {
        when(userRepository.findById("user1")).thenReturn(Optional.of(mockUser));
        when(userRepository.save(mockUser)).thenReturn(mockUser); // <-- fixed

        adminUserService.disableUser("user1");

        assertTrue(mockUser.isDisabled());
        verify(userRepository, times(1)).findById("user1");
        verify(userRepository, times(1)).save(mockUser);
    }


    @Test
    void testDisableUser_notFound() {
        when(userRepository.findById("userX")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                adminUserService.disableUser("userX"));

        assertEquals("User not found", ex.getMessage());
        verify(userRepository, times(1)).findById("userX");
        verify(userRepository, never()).save(any());
    }

    // ----------------- enableUser -----------------
    @Test
    void testEnableUser_success() {
        mockUser.setDisabled(true);
        when(userRepository.findById("user1")).thenReturn(Optional.of(mockUser));
        when(userRepository.save(mockUser)).thenReturn(mockUser); // <-- fixed

        adminUserService.enableUser("user1");

        assertFalse(mockUser.isDisabled());
        verify(userRepository, times(1)).findById("user1");
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testEnableUser_notFound() {
        when(userRepository.findById("userX")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                adminUserService.enableUser("userX"));

        assertEquals("User not found", ex.getMessage());
        verify(userRepository, times(1)).findById("userX");
        verify(userRepository, never()).save(any());
    }

    // ----------------- isDisabled -----------------
    @Test
    void testIsDisabled_true() {
        mockUser.setDisabled(true);
        when(userRepository.findById("user1")).thenReturn(Optional.of(mockUser));

        boolean disabled = adminUserService.isDisabled("user1");

        assertTrue(disabled);
        verify(userRepository, times(1)).findById("user1");
    }

    @Test
    void testIsDisabled_false() {
        mockUser.setDisabled(false);
        when(userRepository.findById("user1")).thenReturn(Optional.of(mockUser));

        boolean disabled = adminUserService.isDisabled("user1");

        assertFalse(disabled);
        verify(userRepository, times(1)).findById("user1");
    }

    @Test
    void testIsDisabled_userNotFound() {
        when(userRepository.findById("userX")).thenReturn(Optional.empty());

        boolean disabled = adminUserService.isDisabled("userX");

        assertFalse(disabled);
        verify(userRepository, times(1)).findById("userX");
    }
}
