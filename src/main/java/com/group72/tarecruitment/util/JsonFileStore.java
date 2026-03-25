package com.group72.tarecruitment.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonFileStore {
    private final ObjectMapper objectMapper;

    public JsonFileStore() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public <T> List<T> readList(Path path, Class<T> itemType) {
        if (Files.notExists(path)) {
            return new ArrayList<>();
        }

        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, itemType);
        try (InputStream inputStream = Files.newInputStream(path)) {
            List<T> values = objectMapper.readValue(inputStream, listType);
            return values == null ? new ArrayList<>() : new ArrayList<>(values);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read JSON file: " + path, exception);
        }
    }

    public <T> void writeList(Path path, List<T> values) {
        try {
            Files.createDirectories(path.getParent());
            try (OutputStream outputStream = Files.newOutputStream(path)) {
                objectMapper.writeValue(outputStream, values);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to write JSON file: " + path, exception);
        }
    }
}
