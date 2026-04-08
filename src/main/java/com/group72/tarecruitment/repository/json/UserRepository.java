package com.group72.tarecruitment.repository.json;

import com.group72.tarecruitment.config.AppConfig;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.util.JsonFileStore;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository extends JsonRepository<User> {
    public UserRepository() {
        this(new JsonFileStore(true), AppConfig.resolveDataFile("users.json"));
    }

    public UserRepository(Path filePath) {
        this(new JsonFileStore(true), filePath);
    }

    protected UserRepository(JsonFileStore fileStore, Path filePath) {
        super(fileStore, filePath, User.class);
    }

    public List<User> findAll() {
        return new ArrayList<>(findAllInternal());
    }

    public Optional<User> findByUsername(String username) {
        return findAllInternal().stream()
                .filter(user -> user.getUsername() != null && user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public Optional<User> findById(String userId) {
        return findAllInternal().stream()
                .filter(user -> user.getId() != null && user.getId().equals(userId))
                .findFirst();
    }

    public void save(User user) {
        List<User> users = findAllInternal();
        Optional<User> existing = users.stream()
                .filter(item -> item.getId() != null && item.getId().equals(user.getId()))
                .findFirst();

        existing.ifPresent(users::remove);
        users.add(user);
        saveAllInternal(users);
    }
}
