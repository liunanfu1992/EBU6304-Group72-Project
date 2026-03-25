package com.group72.tarecruitment.service;

import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.ProfileUpdateResult;
import com.group72.tarecruitment.model.User;
import com.group72.tarecruitment.repository.json.ProfileRepository;
import com.group72.tarecruitment.util.FormUtils;
import com.group72.tarecruitment.util.SkillCatalog;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ProfileService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile getOrCreateProfile(User user) {
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElse(new Profile(user.getId(), "", "", "", user.getEmail(), new ArrayList<>(), new ArrayList<>(), null));
        if (isBlank(profile.getEmail())) {
            profile.setEmail(user.getEmail());
        }
        profile.setSelectedSkills(SkillCatalog.normalizeSelectedSkills(profile.getSelectedSkills()));
        profile.setCustomSkills(SkillCatalog.normalizeCustomSkills(profile.getCustomSkills(), profile.getSelectedSkills()));
        return profile;
    }

    public List<String> getAvailableSkills() {
        return SkillCatalog.PREDEFINED_SKILLS;
    }

    public ProfileUpdateResult updateProfile(User user, String name, String studentId, String major, String email,
                                             String[] selectedSkills, String customSkillsInput) {
        Profile profile = getOrCreateProfile(user);
        profile.setName(safeTrim(name));
        profile.setStudentId(safeTrim(studentId));
        profile.setMajor(safeTrim(major));
        profile.setEmail(safeTrim(email));
        profile.setSelectedSkills(SkillCatalog.normalizeSelectedSkills(selectedSkills == null ? List.of() : List.of(selectedSkills)));
        profile.setCustomSkills(SkillCatalog.normalizeCustomSkills(
                FormUtils.parseTags(customSkillsInput),
                profile.getSelectedSkills()
        ));

        List<String> errors = validateProfile(profile);
        if (!errors.isEmpty()) {
            return new ProfileUpdateResult(false, profile, errors);
        }

        profileRepository.save(profile);
        return new ProfileUpdateResult(true, profile, List.of());
    }

    private List<String> validateProfile(Profile profile) {
        List<String> errors = new ArrayList<>();

        if (isBlank(profile.getName())) {
            errors.add("Name is required.");
        }
        if (isBlank(profile.getStudentId())) {
            errors.add("Student ID is required.");
        }
        if (isBlank(profile.getMajor())) {
            errors.add("Major is required.");
        }
        if (isBlank(profile.getEmail())) {
            errors.add("Email is required.");
        } else if (!EMAIL_PATTERN.matcher(profile.getEmail()).matches()) {
            errors.add("Email must be a valid address.");
        }

        return errors;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
