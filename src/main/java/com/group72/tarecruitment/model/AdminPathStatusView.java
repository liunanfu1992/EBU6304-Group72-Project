package com.group72.tarecruitment.model;

public class AdminPathStatusView {
    private final String label;
    private final String path;
    private final boolean present;
    private final String detail;

    public AdminPathStatusView(String label, String path, boolean present, String detail) {
        this.label = label;
        this.path = path;
        this.present = present;
        this.detail = detail;
    }

    public String getLabel() {
        return label;
    }

    public String getPath() {
        return path;
    }

    public boolean isPresent() {
        return present;
    }

    public String getDetail() {
        return detail;
    }

    public String getStatusLabel() {
        return present ? "Ready" : "Missing";
    }

    public String getStatusTagClass() {
        return present ? "tag" : "tag tag-muted";
    }
}
