package com.group72.tarecruitment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.group72.tarecruitment.model.CvUploadResult;
import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.service.CvService;
import com.group72.tarecruitment.util.LocalDataCipher;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CvServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void uploadCvShouldStoreAllowedFileAndUpdateProfile() throws Exception {
        Path profileFile = tempDir.resolve("profiles.json");
        Path cvDir = tempDir.resolve("storage/cv");
        CvService service = new CvService(new ProfileRepository(profileFile), cvDir);
        User user = new User("ta-1", "alice", "", Role.TA, "alice@example.com");

        CvUploadResult result = service.uploadCv(
                user,
                "alice_cv.PDF",
                128,
                new ByteArrayInputStream("pdf-content".getBytes(StandardCharsets.UTF_8))
        );

        assertTrue(result.isSuccess());
        assertTrue(result.getStoredFileName().startsWith("ta-1-"));
        assertTrue(result.getStoredFileName().endsWith(".pdf"));
        assertTrue(Files.exists(cvDir.resolve(result.getStoredFileName())));
        assertTrue(LocalDataCipher.isEncryptedPayload(Files.readAllBytes(cvDir.resolve(result.getStoredFileName()))));

        Profile profile = service.getOrCreateProfile(user);
        assertEquals(result.getStoredFileName(), profile.getCvPath());
        assertTrue(profile.hasCv());
        assertEquals(
                "pdf-content",
                new String(service.readStoredCvBytes(profile).orElseThrow(), StandardCharsets.UTF_8)
        );
    }

    @Test
    void uploadCvShouldRejectUnsupportedExtension() {
        CvService service = new CvService(
                new ProfileRepository(tempDir.resolve("profiles.json")),
                tempDir.resolve("storage/cv")
        );
        User user = new User("ta-2", "bob", "", Role.TA, "bob@example.com");

        CvUploadResult result = service.uploadCv(
                user,
                "bob.exe",
                32,
                new ByteArrayInputStream("bad".getBytes(StandardCharsets.UTF_8))
        );

        assertFalse(result.isSuccess());
        assertTrue(result.getErrors().contains("Only PDF, DOC, and DOCX files are allowed."));
    }

    @Test
    void uploadCvShouldRejectOversizedFile() {
        CvService service = new CvService(
                new ProfileRepository(tempDir.resolve("profiles.json")),
                tempDir.resolve("storage/cv")
        );
        User user = new User("ta-3", "carol", "", Role.TA, "carol@example.com");

        CvUploadResult result = service.uploadCv(
                user,
                "carol.docx",
                CvService.MAX_CV_SIZE_BYTES + 1,
                new ByteArrayInputStream(new byte[0])
        );

        assertFalse(result.isSuccess());
        assertTrue(result.getErrors().contains("CV file size must be 5MB or smaller."));
    }

    @Test
    void uploadCvShouldReplacePreviousFileForSameUser() throws Exception {
        Path profileFile = tempDir.resolve("profiles.json");
        Path cvDir = tempDir.resolve("storage/cv");
        ProfileRepository profileRepository = new ProfileRepository(profileFile);
        CvService service = new CvService(profileRepository, cvDir);
        User user = new User("ta-4", "dave", "", Role.TA, "dave@example.com");

        CvUploadResult firstResult = service.uploadCv(
                user,
                "dave.doc",
                64,
                new ByteArrayInputStream("first".getBytes(StandardCharsets.UTF_8))
        );
        assertTrue(firstResult.isSuccess());

        Path firstFile = cvDir.resolve(firstResult.getStoredFileName());
        assertTrue(Files.exists(firstFile));

        Thread.sleep(2);

        CvUploadResult secondResult = service.uploadCv(
                user,
                "dave_new.docx",
                64,
                new ByteArrayInputStream("second".getBytes(StandardCharsets.UTF_8))
        );

        assertTrue(secondResult.isSuccess());
        assertFalse(Files.exists(firstFile));
        assertTrue(Files.exists(cvDir.resolve(secondResult.getStoredFileName())));
        assertEquals(
                List.of(secondResult.getStoredFileName()),
                Files.list(cvDir).map(path -> path.getFileName().toString()).sorted().toList()
        );
    }

    @Test
    void resolveStoredCvShouldReturnExistingFileInsideStorageDirectory() throws Exception {
        Path profileFile = tempDir.resolve("profiles.json");
        Path cvDir = tempDir.resolve("storage/cv");
        CvService service = new CvService(new ProfileRepository(profileFile), cvDir);
        User user = new User("ta-5", "erin", "", Role.TA, "erin@example.com");

        CvUploadResult uploadResult = service.uploadCv(
                user,
                "erin.docx",
                128,
                new ByteArrayInputStream("resume".getBytes(StandardCharsets.UTF_8))
        );

        assertTrue(uploadResult.isSuccess());
        Profile profile = service.getOrCreateProfile(user);
        assertTrue(service.resolveStoredCv(profile).isPresent());
        assertEquals(cvDir.resolve(uploadResult.getStoredFileName()), service.resolveStoredCv(profile).orElseThrow());
    }

    @Test
    void resolveStoredCvShouldRejectInvalidProfilePath() {
        CvService service = new CvService(
                new ProfileRepository(tempDir.resolve("profiles.json")),
                tempDir.resolve("storage/cv")
        );
        Profile profile = new Profile("ta-6", "frank", "", "", "frank@example.com", List.of(), List.of(), "../outside.pdf");

        assertTrue(service.resolveStoredCv(profile).isEmpty());
    }

    @Test
    void deleteCvShouldRemoveStoredFileAndClearProfileReference() throws Exception {
        Path profileFile = tempDir.resolve("profiles.json");
        Path cvDir = tempDir.resolve("storage/cv");
        CvService service = new CvService(new ProfileRepository(profileFile), cvDir);
        User user = new User("ta-7", "gina", "", Role.TA, "gina@example.com");

        CvUploadResult uploadResult = service.uploadCv(
                user,
                "gina.pdf",
                128,
                new ByteArrayInputStream("resume".getBytes(StandardCharsets.UTF_8))
        );
        assertTrue(uploadResult.isSuccess());

        Path storedFile = cvDir.resolve(uploadResult.getStoredFileName());
        assertTrue(Files.exists(storedFile));

        boolean deleted = service.deleteCv(user);

        assertTrue(deleted);
        assertFalse(Files.exists(storedFile));
        assertFalse(service.getOrCreateProfile(user).hasCv());
    }

    @Test
    void deleteCvShouldReturnFalseWhenNoCurrentCvExists() {
        CvService service = new CvService(
                new ProfileRepository(tempDir.resolve("profiles.json")),
                tempDir.resolve("storage/cv")
        );
        User user = new User("ta-8", "henry", "", Role.TA, "henry@example.com");

        assertFalse(service.deleteCv(user));
    }
}
