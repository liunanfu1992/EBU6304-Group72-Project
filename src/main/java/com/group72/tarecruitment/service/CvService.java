package com.group72.tarecruitment.service;

import com.group72.tarecruitment.config.AppConfig;
import com.group72.tarecruitment.model.CvUploadResult;
import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

public class CvService {
    public static final long MAX_CV_SIZE_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "doc", "docx");

    private final ProfileRepository profileRepository;
    private final Path cvStorageDir;

    public CvService(ProfileRepository profileRepository) {
        this(profileRepository, AppConfig.getCvStorageDir());
    }

    public CvService(ProfileRepository profileRepository, Path cvStorageDir) {
        this.profileRepository = profileRepository;
        this.cvStorageDir = cvStorageDir;
    }

    public Profile getOrCreateProfile(User user) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElse(new Profile(user.getId(), "", "", "", user.getEmail(), new ArrayList<>(), new ArrayList<>(), null));
        if (isBlank(profile.getEmail())) {
            profile.setEmail(user.getEmail());
        }
        return profile;
    }

    public CvUploadResult uploadCv(User user, String submittedFileName, long fileSize, InputStream inputStream) {
        Profile profile = getOrCreateProfile(user);
        List<String> errors = validateUpload(submittedFileName, fileSize);
        if (!errors.isEmpty()) {
            return new CvUploadResult(false, profile, errors, null);
        }

        String extension = extractExtension(submittedFileName);
        String storedFileName = buildStoredFileName(user.getId(), extension);
        Path targetFile = cvStorageDir.resolve(storedFileName).normalize();
        if (!targetFile.startsWith(cvStorageDir.normalize())) {
            return new CvUploadResult(false, profile, List.of("Invalid storage target for uploaded CV."), null);
        }

        try {
            Files.createDirectories(cvStorageDir);
            deleteExistingCvIfPresent(profile);
            Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            profile.setCvPath(storedFileName);
            profileRepository.save(profile);
            return new CvUploadResult(true, profile, List.of(), storedFileName);
        } catch (IOException exception) {
            return new CvUploadResult(false, profile, List.of("Failed to store CV file."), null);
        }
    }

    public String getAllowedExtensionsDisplay() {
        StringJoiner joiner = new StringJoiner(", ");
        for (String extension : ALLOWED_EXTENSIONS) {
            joiner.add(extension.toUpperCase(Locale.ROOT));
        }
        return joiner.toString();
    }

    public long getMaxCvSizeMb() {
        return MAX_CV_SIZE_BYTES / (1024 * 1024);
    }

    public Optional<Path> resolveStoredCv(Profile profile) {
        if (profile == null || isBlank(profile.getCvPath())) {
            return Optional.empty();
        }

        Path resolvedFile = cvStorageDir.resolve(profile.getCvPath()).normalize();
        if (!resolvedFile.startsWith(cvStorageDir.normalize()) || !Files.isRegularFile(resolvedFile)) {
            return Optional.empty();
        }

        return Optional.of(resolvedFile);
    }

    public String getDownloadFileName(Profile profile) {
        if (profile == null || isBlank(profile.getCvPath())) {
            return "cv";
        }
        return Path.of(profile.getCvPath()).getFileName().toString();
    }

    public String getContentType(String fileName) {
        String extension = extractExtension(fileName);
        if ("pdf".equals(extension)) {
            return "application/pdf";
        }
        if ("doc".equals(extension)) {
            return "application/msword";
        }
        if ("docx".equals(extension)) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        return "application/octet-stream";
    }

    private List<String> validateUpload(String submittedFileName, long fileSize) {
        List<String> errors = new ArrayList<>();
        String extension = extractExtension(submittedFileName);

        if (isBlank(submittedFileName) || fileSize <= 0) {
            errors.add("Please choose a CV file to upload.");
            return errors;
        }
        if (isBlank(extension) || !ALLOWED_EXTENSIONS.contains(extension)) {
            errors.add("Only PDF, DOC, and DOCX files are allowed.");
        }
        if (fileSize > MAX_CV_SIZE_BYTES) {
            errors.add("CV file size must be 5MB or smaller.");
        }

        return errors;
    }

    private void deleteExistingCvIfPresent(Profile profile) throws IOException {
        if (isBlank(profile.getCvPath())) {
            return;
        }

        Path existingFile = cvStorageDir.resolve(profile.getCvPath()).normalize();
        if (existingFile.startsWith(cvStorageDir.normalize())) {
            Files.deleteIfExists(existingFile);
        }
    }

    private String buildStoredFileName(String userId, String extension) {
        return userId + "-" + Instant.now().toEpochMilli() + "." + extension;
    }

    private String extractExtension(String submittedFileName) {
        if (submittedFileName == null) {
            return "";
        }

        String normalizedName = submittedFileName.replace("\\", "/");
        int slashIndex = normalizedName.lastIndexOf('/');
        String baseName = slashIndex >= 0 ? normalizedName.substring(slashIndex + 1) : normalizedName;
        int dotIndex = baseName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == baseName.length() - 1) {
            return "";
        }
        return baseName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
