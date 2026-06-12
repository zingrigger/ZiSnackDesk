package com.bosyon.zisnackdesk.archive.domain;

public enum ApplicationStatus {
    DRAFT(0, "草稿"),
    SUBMITTED(1, "已提交"),
    APPROVED(2, "已通过"),
    REJECTED(3, "已驳回"),
    CANCELLED(4, "已撤销");

    private final int code;
    private final String label;

    ApplicationStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() { return code; }
    public String getLabel() { return label; }

    public static ApplicationStatus fromCode(int code) {
        for (ApplicationStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("未知状态码: " + code);
    }
}
