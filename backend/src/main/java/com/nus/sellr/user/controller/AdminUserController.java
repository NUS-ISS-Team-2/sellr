package com.nus.sellr.user.controller;

import com.nus.sellr.user.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<?> disableUser(@PathVariable String id) {
        adminUserService.disableUser(id);
        return ResponseEntity.ok("User disabled");
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<?> enableUser(@PathVariable String id) {
        adminUserService.enableUser(id);
        return ResponseEntity.ok("User enabled");
    }
}