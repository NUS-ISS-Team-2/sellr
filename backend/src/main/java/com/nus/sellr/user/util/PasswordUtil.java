package com.nus.sellr.user.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public static String hashPassword(String plainPassword) {
        return ENCODER.encode(plainPassword);
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return ENCODER.matches(plainPassword, hashedPassword);
    }
}