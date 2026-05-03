package com.group72.tarecruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.ProfileUpdateResult;
import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.service.ProfileService;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProfileServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void updateProfileShouldNormalizeStructuredSkills() {
        ProfileService service = new ProfileService(new ProfileRepository(tempDir.resolve("profiles.json")));
        User user = new User("ta-1", "alice", "", Role.TA, "alice@example.com");

        ProfileUpdateResult result = service.updateProfile(
                user,
                "Alice",
                "20260001",
                "Computer Science",
                "alice@example.com",
                new String[]{"Java", "Python", "Java", "Unknown"},
                "Teamwork, Research, Java, Research"
        );

        assertTrue(result.isSuccess());

        Profile savedProfile = service.getOrCreateProfile(user);
        assertEquals(List.of("Java", "Python"), savedProfile.getSelectedSkills());
        assertEquals(List.of("Research"), savedProfile.getCustomSkills());
        assertEquals("alice@example.com", savedProfile.getEmail());
    }

    @Test
    void updateProfileShouldRejectInvalidEmailAndKeepSelectedSkills() {
        ProfileService service = new ProfileService(new ProfileRepository(tempDir.resolve("profiles.json")));
        User user = new User("ta-2", "bob", "", Role.TA, "bob@example.com");

        ProfileUpdateResult result = service.updateProfile(
                user,
                "Bob",
                "20260002",
                "Software Engineering",
                "not-an-email",
                new String[]{"Communication"},
                "Mentoring"
        );

        assertFalse(result.isSuccess());
        assertTrue(result.getErrors().contains("Email must be a valid address."));
        assertEquals(List.of("Communication"), result.getProfile().getSelectedSkills());
        assertEquals(List.of("Mentoring"), result.getProfile().getCustomSkills());
    }

    @Test
    void getOrCreateProfileShouldPrefillEmailFromUser() {
        ProfileRepository profileRepository = new ProfileRepository(tempDir.resolve("profiles.json"));
        ProfileService service = new ProfileService(profileRepository);
        User user = new User("ta-3", "carol", "", Role.TA, "carol@example.com");

        Profile profile = service.getOrCreateProfile(user);

        assertEquals("carol@example.com", profile.getEmail());
        assertEquals("ta-3", profile.getUserId());
    }
}
