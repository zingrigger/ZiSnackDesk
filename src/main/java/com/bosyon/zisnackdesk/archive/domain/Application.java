package com.bosyon.zisnackdesk.archive.domain;

import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationResponse;

import java.time.LocalDateTime;

public class Application {

    private Long id;
    private Long applicantId;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime updatedAt;

    // --- Constructor ---

    public Application() {}

    public Application(Long applicantId, Integer status) {
        this.applicantId = applicantId;
        this.status = status;
    }

    // --- Behavior ---

    public void validateForCreate() {
        if (applicantId == null) {
            throw new IllegalArgumentException("申请人不能为空");
        }
        if (status == null) {
            throw new IllegalArgumentException("状态不能为空");
        }
    }

    public void validateForUpdate() {
        if (id == null) {
            throw new IllegalArgumentException("ID 不能为空");
        }
    }

    public void submit() {
        if (status != ApplicationStatus.DRAFT.getCode()) {
            throw new IllegalStateException("只有草稿状态的申请单可以提交");
        }
        this.status = ApplicationStatus.SUBMITTED.getCode();
    }

    public void approve() {
        if (status != ApplicationStatus.SUBMITTED.getCode()) {
            throw new IllegalStateException("只有已提交状态的申请单可以通过");
        }
        this.status = ApplicationStatus.APPROVED.getCode();
    }

    public void reject() {
        if (status != ApplicationStatus.SUBMITTED.getCode()) {
            throw new IllegalStateException("只有已提交状态的申请单可以驳回");
        }
        this.status = ApplicationStatus.REJECTED.getCode();
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public ApplicationResponse toVO() {
        ApplicationResponse vo = new ApplicationResponse();
        vo.setId(this.id);
        vo.setApplicantId(this.applicantId);
        vo.setStatus(this.status);
        vo.setCreatedAt(this.createdAt);
        vo.setCreatedBy(this.createdBy);
        vo.setUpdatedBy(this.updatedBy);
        vo.setUpdatedAt(this.updatedAt);
        return vo;
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getApplicantId() { return applicantId; }
    public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
