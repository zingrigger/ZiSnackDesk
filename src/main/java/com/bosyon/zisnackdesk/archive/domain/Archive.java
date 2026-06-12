package com.bosyon.zisnackdesk.archive.domain;

import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveResponse;

import java.time.LocalDateTime;

public class Archive {

    private Long id;
    private Integer status;
    private Long currentApplicationId;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // --- Constructor ---

    public Archive() {}

    public Archive(Integer status, Long currentApplicationId) {
        this.status = status;
        this.currentApplicationId = currentApplicationId;
    }

    // --- Behavior ---

    public void validateForCreate() {
        if (status == null || status < 0) {
            throw new IllegalArgumentException("状态值不合法");
        }
        if (currentApplicationId == null) {
            throw new IllegalArgumentException("关联申请单不能为空");
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

    public ArchiveResponse toVO() {
        ArchiveResponse vo = new ArchiveResponse();
        vo.setId(this.id);
        vo.setStatus(this.status);
        vo.setCurrentApplicationId(this.currentApplicationId);
        vo.setCreatedBy(this.createdBy);
        vo.setUpdatedBy(this.updatedBy);
        vo.setCreatedAt(this.createdAt);
        vo.setUpdatedAt(this.updatedAt);
        return vo;
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Long getCurrentApplicationId() { return currentApplicationId; }
    public void setCurrentApplicationId(Long currentApplicationId) { this.currentApplicationId = currentApplicationId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
