package com.group72.tarecruitment.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public final class SkillCatalog {
    public static final List<String> PREDEFINED_SKILLS = List.of(
            "Java",
            "Python",
            "Data Structures",
            "Algorithms",
            "Machine Learning",
            "Communication",
            "Marking",
            "Grading Experience",
            "Presentation",
            "Teamwork",
            "Project Coordination",
            "Web Development"
    );
    private static final Map<String, String> PREDEFINED_SKILL_LOOKUP = PREDEFINED_SKILLS.stream()
            .collect(Collectors.toUnmodifiableMap(
                    skill -> skill.toLowerCase(Locale.ROOT),
                    skill -> skill
            ));

    private SkillCatalog() {
    }

    public static List<String> normalizeSelectedSkills(Collection<String> rawSkills) {
        if (rawSkills == null || rawSkills.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> normalized = new ArrayList<>();
        for (String rawSkill : rawSkills) {
            String canonicalSkill = canonicalizePredefined(rawSkill);
            if (canonicalSkill != null && !normalized.contains(canonicalSkill)) {
                normalized.add(canonicalSkill);
            }
        }
        return normalized;
    }

    public static List<String> normalizeCustomSkills(Collection<String> rawSkills, Collection<String> excludedSkills) {
        if (rawSkills == null || rawSkills.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> normalized = new ArrayList<>();
        List<String> excluded = excludedSkills == null ? List.of() : excludedSkills.stream()
                .map(SkillCatalog::normalizeLabel)
                .filter(value -> !value.isEmpty())
                .toList();

        for (String rawSkill : rawSkills) {
            String value = normalizeLabel(rawSkill);
            if (value.isEmpty()) {
                continue;
            }

            if (isPredefinedSkill(value) || containsIgnoreCase(excluded, value) || containsIgnoreCase(normalized, value)) {
                continue;
            }

            normalized.add(value);
        }

        return normalized;
    }

    public static List<String> mergeSkills(Collection<String> predefinedSkills, Collection<String> customSkills) {
        List<String> merged = new ArrayList<>();
        if (predefinedSkills != null) {
            merged.addAll(predefinedSkills);
        }
        if (customSkills != null) {
            for (String customSkill : customSkills) {
                if (!containsIgnoreCase(merged, customSkill)) {
                    merged.add(customSkill);
                }
            }
        }
        return merged;
    }

    public static List<String> extractPredefinedSkills(Collection<String> skills) {
        return normalizeSelectedSkills(skills);
    }

    public static List<String> extractCustomSkills(Collection<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> customSkills = new ArrayList<>();
        for (String skill : skills) {
            String value = normalizeLabel(skill);
            if (value.isEmpty() || isPredefinedSkill(value) || containsIgnoreCase(customSkills, value)) {
                continue;
            }
            customSkills.add(value);
        }
        return customSkills;
    }

    public static String joinSkills(Collection<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return "";
        }

        StringJoiner joiner = new StringJoiner(", ");
        for (String skill : skills) {
            String value = normalizeLabel(skill);
            if (!value.isEmpty()) {
                joiner.add(value);
            }
        }
        return joiner.toString();
    }

    public static boolean isPredefinedSkill(String skill) {
        return canonicalizePredefined(skill) != null;
    }

    private static String canonicalizePredefined(String skill) {
        String normalized = normalizeLabel(skill);
        if (normalized.isEmpty()) {
            return null;
        }
        return PREDEFINED_SKILL_LOOKUP.get(normalized.toLowerCase(Locale.ROOT));
    }

    private static boolean containsIgnoreCase(Collection<String> values, String candidate) {
        String normalizedCandidate = normalizeLabel(candidate);
        if (normalizedCandidate.isEmpty()) {
            return false;
        }

        for (String value : values) {
            if (normalizedCandidate.equalsIgnoreCase(normalizeLabel(value))) {
                return true;
            }
        }
        return false;
    }

    private static String normalizeLabel(String value) {
        return value == null ? "" : value.trim();
    }
}
