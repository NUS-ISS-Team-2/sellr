package com.nus.sellr.user.util;

import com.nus.sellr.user.entity.Admin;
import com.nus.sellr.user.entity.Buyer;
import com.nus.sellr.user.entity.Seller;
import com.nus.sellr.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private final Key jwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // secure random key
    private final long jwtExpirationMs = 86400000; // 1 day

    String role;



    public String generateToken(User user) {

        if (user instanceof Admin) {
            role = "ADMIN";
        } else if (user instanceof Seller) {
            role = "SELLER";
        } else if (user instanceof Buyer) {
            role = "BUYER";
        } else {
            role = "USER"; // fallback
        }

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtKey) // use the Key object, not a string
                .compact();
    }
}
