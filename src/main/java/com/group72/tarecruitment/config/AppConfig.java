package com.group72.tarecruitment.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.ServletContext;

public final class AppConfig {
    public static final String APP_HOME_PROPERTY = "ta.app.home";
    private static Path appHome;

    private AppConfig() {
    }

    public static synchronized void initialize(ServletContext servletContext) {
        if (appHome != null) {
            return;
        }

        String configuredHome = System.getProperty(APP_HOME_PROPERTY);
        if (isBlank(configuredHome)) {
            configuredHome = System.getenv("TA_APP_HOME");
        }

        if (isBlank(configuredHome)) {
            String catalinaBase = System.getProperty("catalina.base");
            if (!isBlank(catalinaBase)) {
                configuredHome = Paths.get(catalinaBase, "work", "ta-recruitment-system-data").toString();
            } else {
                configuredHome = Paths.get(System.getProperty("user.dir"), ".ta-recruitment-system").toString();
            }
        }

        appHome = Paths.get(configuredHome).toAbsolutePath().normalize();
        ensureDirectory(getDataDir());
        ensureDirectory(getStorageDir());
        ensureDirectory(getCvStorageDir());
        servletContext.setAttribute("appHome", appHome.toString());
    }

    public static Path getAppHome() {
        requireInitialization();
        return appHome;
    }

    public static Path getDataDir() {
        requireInitialization();
        return appHome.resolve("data");
    }

    public static Path getStorageDir() {
        requireInitialization();
        return appHome.resolve("storage");
    }

    public static Path getCvStorageDir() {
        requireInitialization();
        return getStorageDir().resolve("cv");
    }

    public static Path resolveDataFile(String fileName) {
        return getDataDir().resolve(fileName);
    }

    private static void ensureDirectory(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to create directory: " + path, exception);
        }
    }

    private static void requireInitialization() {
        if (appHome == null) {
            throw new IllegalStateException("AppConfig has not been initialized.");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
