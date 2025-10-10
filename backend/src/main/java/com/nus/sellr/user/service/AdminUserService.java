package com.nus.sellr.user.service;

import com.nus.sellr.user.entity.User;
import com.nus.sellr.user.repository.UserRepositoryImpl;
import org.springframework.stereotype.Service;

@Service
public class AdminUserService {

    private final UserRepositoryImpl userRepository;

    public AdminUserService(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    public void disableUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDisabled(true);
        userRepository.save(user); // save method needed in UserRepositoryImpl
    }

    public void enableUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDisabled(false);
        userRepository.save(user);
    }

    public boolean isDisabled(String userId) {
        return userRepository.findById(userId)
                .map(User::isDisabled)
                .orElse(false);
    }
}
