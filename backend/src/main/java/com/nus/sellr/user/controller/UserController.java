package com.nus.sellr.user.controller;

import com.nus.sellr.user.dto.LoginRequest;
import com.nus.sellr.user.dto.CreateUserRequest;
import com.nus.sellr.user.dto.CreateUserResponse;
import com.nus.sellr.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        CreateUserResponse createdUser = userService.createUser(request);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hello, Sellr backend is running!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser (@RequestBody LoginRequest request) {
        return ResponseEntity.ok("test");
    }
}
