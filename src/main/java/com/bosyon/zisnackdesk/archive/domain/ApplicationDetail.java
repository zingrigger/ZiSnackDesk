package com.bosyon.zisnackdesk.archive.domain;

import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailResponse;

import java.time.LocalDateTime;

public class ApplicationDetail {

    private Long id;
    private Long applicationId;
    private Long archiveId;
    private LocalDateTime deletedAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- Constructor ---

    public ApplicationDetail() {}

    public ApplicationDetail(Long applicationId, Long archiveId) {
        this.applicationId = applicationId;
        this.archiveId = archiveId;
    }

    // --- Behavior ---

    public void validateForCreate() {
        if (applicationId == null) {
            throw new IllegalArgumentException("申请单 ID 不能为空");
        }
        if (archiveId == null) {
            throw new IllegalArgumentException("档案 ID 不能为空");
        }
    }

    public void validateForUpdate() {
        if (id == null) {
            throw new IllegalArgumentException("ID 不能为空");
        }
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public ApplicationDetailResponse toVO() {
        ApplicationDetailResponse vo = new ApplicationDetailResponse();
        vo.setId(this.id);
        vo.setApplicationId(this.applicationId);
        vo.setArchiveId(this.archiveId);
        vo.setCreatedAt(this.createdAt);
        vo.setCreatedBy(this.createdBy);
        vo.setUpdatedBy(this.updatedBy);
        vo.setUpdatedAt(this.updatedAt);
        return vo;
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public Long getArchiveId() { return archiveId; }
    public void setArchiveId(Long archiveId) { this.archiveId = archiveId; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
