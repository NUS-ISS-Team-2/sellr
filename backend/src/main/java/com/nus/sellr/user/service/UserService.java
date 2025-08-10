package com.nus.sellr.user.service;

import com.nus.sellr.user.dto.UserRequest;
import com.nus.sellr.user.dto.UserResponse;
import com.nus.sellr.user.entity.User;
import com.nus.sellr.user.repository.UserRepository;
import com.nus.sellr.user.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Hash password before saving
        String hashedPassword = PasswordUtil.hashPassword(request.getPassword());
        user.setPassword(hashedPassword);

        // Save user to DB
        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail()))
                .collect(Collectors.toList());
    }

    public UserResponse getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail()))
                .orElse(null);
    }
}
