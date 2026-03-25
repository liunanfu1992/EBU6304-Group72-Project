package com.group72.tarecruitment.service;

import com.group72.tarecruitment.model.Role;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.UserRepository;
import com.group72.tarecruitment.util.PasswordUtil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
