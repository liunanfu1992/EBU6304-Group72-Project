package com.group72.tarecruitment.repository.json;

import com.group72.tarecruitment.config.AppConfig;
import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.util.JsonFileStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfileRepository extends JsonRepository<Profile> {
    public ProfileRepository() {
        super(new JsonFileStore(), AppConfig.resolveDataFile("profiles.json"), Profile.class);
    }

    public List<Profile> findAll() {
        return new ArrayList<>(findAllInternal());
    }

    public Optional<Profile> findByUserId(String userId) {
        return findAllInternal().stream()
                .filter(profile -> profile.getUserId() != null && profile.getUserId().equals(userId))
                .findFirst();
    }

    public void save(Profile profile) {
        List<Profile> profiles = findAllInternal();
        Optional<Profile> existing = profiles.stream()
                .filter(item -> item.getUserId() != null && item.getUserId().equals(profile.getUserId()))
                .findFirst();

        existing.ifPresent(profiles::remove);
        profiles.add(profile);
        saveAllInternal(profiles);
    }
}
