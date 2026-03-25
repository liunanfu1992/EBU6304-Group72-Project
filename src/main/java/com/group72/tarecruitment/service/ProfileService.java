package com.group72.tarecruitment.service;

import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.util.FormUtils;
import java.util.ArrayList;

public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile getOrCreateProfile(String userId) {
        return profileRepository.findByUserId(userId)
                .orElse(new Profile(userId, "", "", "", new ArrayList<>(), null));
    }

    public void updateProfile(String userId, String name, String studentId, String major, String skillsInput) {
        Profile profile = getOrCreateProfile(userId);
        profile.setName(name);
        profile.setStudentId(studentId);
        profile.setMajor(major);
        profile.setSkills(FormUtils.parseTags(skillsInput));
        profileRepository.save(profile);
    }
}
