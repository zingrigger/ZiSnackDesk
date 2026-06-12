package com.bosyon.zisnackdesk.user.domain;

import com.bosyon.zisnackdesk.user.interfaces.dto.SysUserResponse;

import java.time.LocalDateTime;

public class SysUser {

    private String id;
    private String account;
    private String mobile;
    private String email;
    private String password;
    private String userType;
    private Boolean mobileVerified;
    private Boolean emailVerified;
    private LocalDateTime deletedAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- Constructor ---

    public SysUser() {}

    public SysUser(String account, String password, String userType) {
        this.account = account;
        this.password = password;
        this.userType = userType != null ? userType : "member";
    }

    // --- Behavior ---

    public void validateForCreate() {
        if (account == null || account.isBlank()) {
            throw new IllegalArgumentException("账号不能为空");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于6位");
        }
        if (mobile != null && !mobile.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("手机号格式不合法");
        }
        if (email != null && !email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            throw new IllegalArgumentException("邮箱格式不合法");
        }
    }

    public void validateForUpdate() {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID 不能为空");
        }
    }

    public void verifyMobile() {
        this.mobileVerified = true;
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public SysUserResponse toVO() {
        SysUserResponse vo = new SysUserResponse();
        vo.setId(this.id);
        vo.setAccount(this.account);
        vo.setMobile(this.mobile);
        vo.setEmail(this.email);
        vo.setUserType(this.userType);
        vo.setMobileVerified(this.mobileVerified);
        vo.setEmailVerified(this.emailVerified);
        vo.setCreatedBy(this.createdBy);
        vo.setUpdatedBy(this.updatedBy);
        vo.setCreatedAt(this.createdAt);
        vo.setUpdatedAt(this.updatedAt);
        return vo;
    }

    // --- Getters & Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public Boolean getMobileVerified() { return mobileVerified; }
    public void setMobileVerified(Boolean mobileVerified) { this.mobileVerified = mobileVerified; }

    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

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
