package com.group72.tarecruitment.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonFileStore {
    private final ObjectMapper objectMapper;
    private final boolean encryptAtRest;

    public JsonFileStore() {
        this(false);
    }

    public JsonFileStore(boolean encryptAtRest) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.encryptAtRest = encryptAtRest;
    }

    public <T> List<T> readList(Path path, Class<T> itemType) {
        if (Files.notExists(path)) {
            return new ArrayList<>();
        }

        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, itemType);
        try {
            byte[] fileBytes = Files.readAllBytes(path);
            if (fileBytes.length == 0 || new String(fileBytes).isBlank()) {
                return new ArrayList<>();
            }
            boolean encryptedPayload = LocalDataCipher.isEncryptedPayload(fileBytes);
            if (encryptAtRest && !encryptedPayload) {
                throw new IllegalStateException("Expected encrypted local data file: " + path);
            }
            byte[] jsonBytes = encryptedPayload ? LocalDataCipher.decrypt(fileBytes) : fileBytes;
            if (jsonBytes.length == 0 || new String(jsonBytes).isBlank()) {
                return new ArrayList<>();
            }
            List<T> values = objectMapper.readValue(jsonBytes, listType);
            return values == null ? new ArrayList<>() : new ArrayList<>(values);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read JSON file: " + path, exception);
        }
    }

    public <T> void writeList(Path path, List<T> values) {
        try {
            Files.createDirectories(path.getParent());
            byte[] jsonBytes = objectMapper.writeValueAsBytes(values);
            byte[] storedBytes = encryptAtRest ? LocalDataCipher.encrypt(jsonBytes) : jsonBytes;
            Files.write(path, storedBytes);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to write JSON file: " + path, exception);
        }
    }
}
