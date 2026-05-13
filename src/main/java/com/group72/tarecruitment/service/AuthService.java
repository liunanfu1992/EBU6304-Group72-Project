package com.group72.tarecruitment.service;

import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.RegistrationResult;
import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.util.PasswordUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class AuthService {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9._-]{3,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public AuthService(UserRepository userRepository) {
        this(userRepository, null);
    }

    public AuthService(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    public void ensureDefaultUsers() {
        if (!userRepository.findAll().isEmpty()) {
            return;
        }

        List<User> defaultUsers = List.of(
                new User(UUID.randomUUID().toString(), "ta-user", PasswordUtil.hash("password123"), Role.TA, "ta@example.com"),
                new User(UUID.randomUUID().toString(), "mo-user", PasswordUtil.hash("password123"), Role.MO, "mo@example.com"),
                new User(UUID.randomUUID().toString(), "admin-user", PasswordUtil.hash("password123"), Role.ADMIN, "admin@example.com")
        );

        defaultUsers.forEach(userRepository::save);
    }

    public Optional<User> authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && PasswordUtil.matches(password, user.get().getPasswordHash())) {
            return user;
        }
        return Optional.empty();
    }

    public RegistrationResult registerAccount(String username, String plainPassword, Role role, String email) {
        return registerAccount(username, plainPassword, plainPassword, role, email);
    }

    public RegistrationResult registerAccount(String username, String plainPassword, String confirmPassword, Role role, String email) {
        List<String> errors = new ArrayList<>();
        String normalizedUsername = safeTrim(username);
        String normalizedEmail = safeTrim(email);

        if (normalizedUsername.isEmpty()) {
            errors.add("Username is required.");
        } else if (!USERNAME_PATTERN.matcher(normalizedUsername).matches()) {
            errors.add("Username must be 3-30 characters and use only letters, numbers, dots, underscores, or hyphens.");
        }
        if (plainPassword == null || plainPassword.isBlank()) {
            errors.add("Password is required.");
        } else if (plainPassword.length() < MIN_PASSWORD_LENGTH) {
            errors.add("Password must be at least 8 characters.");
        }
        if (confirmPassword == null || confirmPassword.isBlank()) {
            errors.add("Password confirmation is required.");
        } else if (plainPassword != null && !plainPassword.equals(confirmPassword)) {
            errors.add("Passwords do not match.");
        }
        if (role == null) {
            errors.add("Role is required.");
        }
        if (normalizedEmail.isEmpty()) {
            errors.add("Email is required.");
        } else if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            errors.add("Email must be a valid address.");
        }
        if (role == Role.ADMIN) {
            errors.add("Admin accounts cannot be registered.");
        }
        if (!normalizedUsername.isEmpty() && userRepository.findByUsername(normalizedUsername).isPresent()) {
            errors.add("Username is already in use.");
        }
        if (!normalizedEmail.isEmpty() && isEmailInUse(normalizedEmail)) {
            errors.add("Email is already in use.");
        }

        if (!errors.isEmpty()) {
            return new RegistrationResult(false, null, null, errors);
        }

        User user = new User(
                UUID.randomUUID().toString(),
                normalizedUsername,
                PasswordUtil.hash(plainPassword),
                role,
                normalizedEmail
        );
        userRepository.save(user);

        Profile profile = null;
        if (role == Role.TA && profileRepository != null) {
            profile = new Profile(user.getId(), "", "", "", normalizedEmail, List.of(), List.of(), null);
            profileRepository.save(profile);
        }

        return new RegistrationResult(true, user, profile, List.of());
    }

    private boolean isEmailInUse(String email) {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(email));
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
