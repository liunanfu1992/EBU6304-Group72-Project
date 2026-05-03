package com.group72.tarecruitment.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public final class InterviewLinkPolicy {
    private InterviewLinkPolicy() {
    }

    public static boolean isBlankOrSafe(String link) {
        return link == null || link.isBlank() || isSafe(link);
    }

    public static boolean isSafe(String link) {
        if (link == null || link.isBlank()) {
            return false;
        }

        try {
            URI uri = new URI(link.trim());
            String scheme = uri.getScheme();
            if (scheme == null) {
                return false;
            }

            String normalizedScheme = scheme.toLowerCase(Locale.ROOT);
            return ("http".equals(normalizedScheme) || "https".equals(normalizedScheme))
                    && uri.getHost() != null
                    && !uri.getHost().isBlank();
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    public static String safeDisplayLink(String link) {
        return isSafe(link) ? link.trim() : "";
    }
}
