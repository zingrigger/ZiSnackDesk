package com.bosyon.zisnackdesk.user.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUserResponse {
    private String id;
    private String account;
    private String mobile;
    private String email;
    private String userType;
    private Boolean mobileVerified;
    private Boolean emailVerified;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
