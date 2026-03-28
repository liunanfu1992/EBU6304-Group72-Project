package com.group72.tarecruitment.util;

import com.group72.tarecruitment.model.Profile;
import com.group72.tarecruitment.model.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public final class DataMigrationTool {
    private DataMigrationTool() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: DataMigrationTool <appHome>");
        }

        Path appHome = Paths.get(args[0]).toAbsolutePath().normalize();
        Path dataDir = appHome.resolve("data");
        Path cvDir = appHome.resolve("storage").resolve("cv");

        JsonFileStore secureStore = new JsonFileStore(true);

        migrateJsonFile(secureStore, dataDir.resolve("users.json"), User.class);
        migrateJsonFile(secureStore, dataDir.resolve("profiles.json"), Profile.class);
        migrateCvFiles(cvDir);

        System.out.println("Data migration finished for " + appHome);
    }

    private static <T> void migrateJsonFile(JsonFileStore secureStore, Path file, Class<T> itemType) {
        JsonFileStore plainStore = new JsonFileStore(false);
        List<T> items = plainStore.readList(file, itemType);
        secureStore.writeList(file, items);
        System.out.println("Migrated " + file.getFileName() + " (" + items.size() + " records)");
    }

    private static void migrateCvFiles(Path cvDir) throws IOException {
        if (Files.notExists(cvDir) || !Files.isDirectory(cvDir)) {
            System.out.println("Skipped CV directory migration because it does not exist: " + cvDir);
            return;
        }

        int migratedCount = 0;
        try (var files = Files.list(cvDir)) {
            for (Path file : files.filter(Files::isRegularFile).toList()) {
                byte[] storedBytes = Files.readAllBytes(file);
                if (LocalDataCipher.isEncryptedPayload(storedBytes)) {
                    continue;
                }
                Files.write(file, LocalDataCipher.encrypt(storedBytes));
                migratedCount++;
            }
        }
        System.out.println("Migrated " + migratedCount + " CV files in " + cvDir);
    }
}
