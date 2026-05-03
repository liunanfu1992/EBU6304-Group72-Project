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

public class AuthService {
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
                new User(UUID.randomUUID().toString(), "ta-demo", PasswordUtil.hash("password123"), Role.TA, "ta@example.com"),
                new User(UUID.randomUUID().toString(), "mo-demo", PasswordUtil.hash("password123"), Role.MO, "mo@example.com"),
                new User(UUID.randomUUID().toString(), "admin-demo", PasswordUtil.hash("password123"), Role.ADMIN, "admin@example.com")
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
        List<String> errors = new ArrayList<>();
        String normalizedUsername = safeTrim(username);
        String normalizedEmail = safeTrim(email);

        if (normalizedUsername.isEmpty()) {
            errors.add("Username is required.");
        }
        if (plainPassword == null || plainPassword.isBlank()) {
            errors.add("Password is required.");
        }
        if (role == null) {
            errors.add("Role is required.");
        }
        if (normalizedEmail.isEmpty()) {
            errors.add("Email is required.");
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
