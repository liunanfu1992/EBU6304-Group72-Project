package com.group72.tarecruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.group72.tarecruitment.model.RegistrationResult;
import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.service.AuthService;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AuthServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void registerAccountShouldCreateTaUserAndBlankProfile() {
        UserRepository userRepository = new UserRepository(tempDir.resolve("users.json"));
        ProfileRepository profileRepository = new ProfileRepository(tempDir.resolve("profiles.json"));
        AuthService authService = new AuthService(userRepository, profileRepository);

        RegistrationResult result = authService.registerAccount("new-ta", "secret123", Role.TA, "new-ta@example.com");

        assertTrue(result.isSuccess());
        assertEquals("new-ta", result.getUser().getUsername());
        assertEquals(Role.TA, result.getUser().getRole());
        assertEquals("new-ta@example.com", result.getUser().getEmail());
        assertTrue(result.getProfile() != null);
        assertEquals("new-ta@example.com", result.getProfile().getEmail());
        assertEquals(1, userRepository.findAll().size());
        assertEquals(1, profileRepository.findAll().size());
    }

    @Test
    void registerAccountShouldRejectAdminRegistrationAndDuplicateUsername() {
        UserRepository userRepository = new UserRepository(tempDir.resolve("users.json"));
        ProfileRepository profileRepository = new ProfileRepository(tempDir.resolve("profiles.json"));
        AuthService authService = new AuthService(userRepository, profileRepository);

        RegistrationResult adminResult = authService.registerAccount("admin-user", "secret123", Role.ADMIN, "admin@example.com");
        assertFalse(adminResult.isSuccess());
        assertTrue(adminResult.getErrors().contains("Admin accounts cannot be registered."));

        assertTrue(authService.registerAccount("dup-user", "secret123", Role.MO, "dup@example.com").isSuccess());
        RegistrationResult duplicateResult = authService.registerAccount("dup-user", "secret123", Role.MO, "dup2@example.com");

        assertFalse(duplicateResult.isSuccess());
        assertTrue(duplicateResult.getErrors().contains("Username is already in use."));
    }
}
