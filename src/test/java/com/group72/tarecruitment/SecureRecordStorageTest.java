package com.group72.tarecruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.util.JsonFileStore;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SecureRecordStorageTest {
    @TempDir
    Path tempDir;

    @Test
    void userAndProfileFilesShouldBeEncryptedAtRest() throws Exception {
        Path usersFile = tempDir.resolve("users.json");
        Path profilesFile = tempDir.resolve("profiles.json");

        UserRepository userRepository = new UserRepository(usersFile);
        ProfileRepository profileRepository = new ProfileRepository(profilesFile);

        userRepository.save(new User("ta-1", "ta-demo", "hashed-password", Role.TA, "ta@example.com"));
        profileRepository.save(new Profile(
                "ta-1",
                "Alice",
                "20260001",
                "Computer Science",
                "alice@example.com",
                List.of("Java"),
                List.of("Mentoring"),
                "ta-1-resume.pdf"
        ));

        String userFileText = Files.readString(usersFile, StandardCharsets.UTF_8);
        String profileFileText = Files.readString(profilesFile, StandardCharsets.UTF_8);

        assertTrue(userFileText.startsWith("ENC$1:"));
        assertTrue(profileFileText.startsWith("ENC$1:"));
        assertTrue(!userFileText.contains("ta@example.com"));
        assertTrue(!profileFileText.contains("Alice"));
    }

    @Test
    void readingPlaintextProfileFileThroughEncryptedRepositoryShouldBeRejected() throws Exception {
        Path profilesFile = tempDir.resolve("profiles.json");
        Files.createDirectories(profilesFile.getParent());
        Files.writeString(
                profilesFile,
                """
                [
                  {
                    "userId": "ta-plain",
                    "name": "Plain User",
                    "studentId": "20260099",
                    "major": "AI",
                    "email": "plain@example.com",
                    "selectedSkills": ["Python"],
                    "customSkills": [],
                    "cvPath": null
                  }
                ]
                """
        );

        ProfileRepository profileRepository = new ProfileRepository(profilesFile);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                profileRepository::findAll
        );
        assertTrue(exception.getMessage().contains("Expected encrypted local data file"));
    }

    @Test
    void blankJsonFileShouldBeTreatedAsEmptyList() throws Exception {
        Path usersFile = tempDir.resolve("users.json");
        Files.writeString(usersFile, "");

        JsonFileStore store = new JsonFileStore(true);

        assertTrue(store.readList(usersFile, User.class).isEmpty());
    }
}
