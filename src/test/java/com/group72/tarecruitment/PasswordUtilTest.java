package com.group72.tarecruitment;

import com.group72.tarecruitment.util.PasswordUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PasswordUtilTest {
    @Test
    void shouldHashAndVerifyPassword() {
        String password = "password123";
        String hash = PasswordUtil.hash(password);

        Assertions.assertNotEquals(password, hash);
        Assertions.assertTrue(PasswordUtil.matches(password, hash));
    }
}
