package com.nus.sellr.user.util;

import com.nus.sellr.user.entity.Admin;
import com.nus.sellr.user.entity.Buyer;
import com.nus.sellr.user.entity.Seller;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private final JwtUtils jwtUtils = new JwtUtils();

    @Test
    void generateToken_ShouldContainCorrectClaimsForAdmin() {
        Admin admin = new Admin("admin1", "admin@example.com", "pw");
        admin.setId("admin123");

        String token = jwtUtils.generateToken(admin);
        assertNotNull(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtUtils.getJwtKeyForTest()) // see note below
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("admin1", claims.getSubject());
        assertEquals("admin123", claims.get("userId"));
        assertEquals("admin@example.com", claims.get("email"));
        assertEquals("ADMIN", claims.get("role"));
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void generateToken_ShouldContainCorrectClaimsForSeller() {
        Seller seller = new Seller("seller1", "seller@example.com", "pw", "Store");
        seller.setId("seller123");

        String token = jwtUtils.generateToken(seller);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtUtils.getJwtKeyForTest())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("SELLER", claims.get("role"));
    }

    @Test
    void generateToken_ShouldContainCorrectClaimsForBuyer() {
        Buyer buyer = new Buyer("buyer1", "buyer@example.com", "pw", 0);
        buyer.setId("buyer123");

        String token = jwtUtils.generateToken(buyer);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtUtils.getJwtKeyForTest())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("BUYER", claims.get("role"));
    }
}
