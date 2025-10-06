package com.nus.sellr.user.controller;

import com.nus.sellr.user.dto.*;
import com.nus.sellr.user.entity.Role;
import com.nus.sellr.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        CreateUserRequest request = new CreateUserRequest();
        CreateUserResponse expectedResponse = new CreateUserResponse();
        when(userService.createUser(ArgumentMatchers.any(CreateUserRequest.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<CreateUserResponse> response = userController.createUser(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void testTestEndpoint() {
        ResponseEntity<String> response = userController.test();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Hello, Sellr backend is running!", response.getBody());
    }

    @Test
    void testLoginUser() {
        LoginRequest request = new LoginRequest();
        LoginResponse expectedResponse = new LoginResponse();
        when(userService.loginUser(ArgumentMatchers.any(LoginRequest.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<LoginResponse> response = userController.loginUser(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedResponse, response.getBody());
    }

    // -------------------
    // getAllUsers()
    // -------------------
    @Test
    void testGetAllUsers_returnsList() {
        List<UserResponse> mockUsers = Arrays.asList(
                new UserResponse("u1", "Alice", "alice@example.com", Role.BUYER),
                new UserResponse("u2", "Bob", "bob@example.com", Role.BUYER)
        );

        when(userService.getAllUsers()).thenReturn(mockUsers);

        ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }

    // -------------------
    // updateUser()
    // -------------------
    @Test
    void testUpdateUser_success() {
        String userId = "u1";
        UpdateUser dto = new UpdateUser();
        dto.setUsername("AliceUpdated");
        dto.setEmail("alice.new@example.com");

        UserResponse updated = new UserResponse(userId, "AliceUpdated", "alice.new@example.com", Role.ADMIN);
        when(userService.updateUser(userId, dto)).thenReturn(updated);

        ResponseEntity<UserResponse> response = userController.updateUser(userId, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("AliceUpdated", response.getBody().getUsername());
        verify(userService, times(1)).updateUser(userId, dto);
    }

    // -------------------
    // changePassword()
    // -------------------
    @Test
    void testChangePassword_success() {
        String userId = "u1";
        Map<String, String> body = new HashMap<>();
        body.put("password", "newPass123");

        ResponseEntity<String> response = userController.changePassword(userId, body);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Password updated successfully", response.getBody());
        verify(userService, times(1)).changePassword(userId, "newPass123");
    }

    @Test
    void testChangePassword_emptyPassword() {
        String userId = "u1";
        Map<String, String> body = new HashMap<>();
        body.put("password", "  "); // blank

        ResponseEntity<String> response = userController.changePassword(userId, body);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Password cannot be empty", response.getBody());
        verify(userService, never()).changePassword(anyString(), anyString());
    }

    // -------------------
    // deleteUser()
    // -------------------
    @Test
    void testDeleteUser_success() {
        String userId = "u1";

        doNothing().when(userService).deleteUser(userId);

        ResponseEntity<String> response = userController.deleteUser(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User deleted successfully", response.getBody());
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void testDeleteUser_notFound() {
        String userId = "u2";

        doThrow(new RuntimeException("User not found")).when(userService).deleteUser(userId);

        ResponseEntity<String> response = userController.deleteUser(userId);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody());
        verify(userService, times(1)).deleteUser(userId);
    }
}
