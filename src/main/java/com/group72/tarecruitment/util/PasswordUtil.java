package com.group72.tarecruitment.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() {
    }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean matches(String plainPassword, String passwordHash) {
        return plainPassword != null && passwordHash != null && BCrypt.checkpw(plainPassword, passwordHash);
    }
}
