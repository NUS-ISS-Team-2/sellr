package com.nus.sellr.user.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void hashAndVerifyPassword_ShouldMatch() {
        String plain = "myPassword123";
        String hashed = PasswordUtil.hashPassword(plain);

        assertNotNull(hashed);
        assertTrue(PasswordUtil.verifyPassword(plain, hashed));
        assertFalse(PasswordUtil.verifyPassword("wrongPassword", hashed));
    }
}
