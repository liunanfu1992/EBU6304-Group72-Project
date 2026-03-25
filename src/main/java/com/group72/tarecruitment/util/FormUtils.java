package com.group72.tarecruitment.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class FormUtils {
    private FormUtils() {
    }

    public static List<String> parseTags(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            return List.of();
        }

        return Arrays.stream(rawInput.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }
}
