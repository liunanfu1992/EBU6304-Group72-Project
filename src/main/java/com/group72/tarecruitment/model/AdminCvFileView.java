package com.group72.tarecruitment.model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class AdminCvFileView {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private final String fileName;
    private final long sizeBytes;
    private final Instant lastModifiedAt;
    private final String ownerUserId;
    private final String ownerDisplayName;

    public AdminCvFileView(String fileName, long sizeBytes, Instant lastModifiedAt, String ownerUserId, String ownerDisplayName) {
        this.fileName = fileName;
        this.sizeBytes = sizeBytes;
        this.lastModifiedAt = lastModifiedAt;
        this.ownerUserId = ownerUserId;
        this.ownerDisplayName = ownerDisplayName;
    }

    public String getFileName() {
        return fileName;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public Instant getLastModifiedAt() {
        return lastModifiedAt;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public String getOwnerDisplayName() {
        return ownerDisplayName;
    }

    public String getSizeDisplay() {
        if (sizeBytes < 1024) {
            return sizeBytes + " B";
        }
        if (sizeBytes < 1024 * 1024) {
            return String.format("%.1f KB", sizeBytes / 1024.0);
        }
        return String.format("%.2f MB", sizeBytes / (1024.0 * 1024.0));
    }

    public String getLastModifiedDisplay() {
        return lastModifiedAt == null ? "-" : DATE_TIME_FORMATTER.format(lastModifiedAt);
    }
}
