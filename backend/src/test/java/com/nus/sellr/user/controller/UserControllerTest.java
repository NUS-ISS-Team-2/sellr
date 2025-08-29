package com.nus.sellr.user.controller;

import com.nus.sellr.user.dto.CreateUserRequest;
import com.nus.sellr.user.dto.CreateUserResponse;
import com.nus.sellr.user.dto.LoginRequest;
import com.nus.sellr.user.dto.LoginResponse;
import com.nus.sellr.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
}
