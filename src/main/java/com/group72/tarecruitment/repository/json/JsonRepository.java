package com.group72.tarecruitment.repository.json;

import com.group72.tarecruitment.util.JsonFileStore;
import java.nio.file.Path;
import java.util.List;

public abstract class JsonRepository<T> {
    private final JsonFileStore fileStore;
    private final Path filePath;
    private final Class<T> itemType;

    protected JsonRepository(JsonFileStore fileStore, Path filePath, Class<T> itemType) {
        this.fileStore = fileStore;
        this.filePath = filePath;
        this.itemType = itemType;
    }

    protected List<T> findAllInternal() {
        return fileStore.readList(filePath, itemType);
    }

    protected void saveAllInternal(List<T> values) {
        fileStore.writeList(filePath, values);
    }
}
