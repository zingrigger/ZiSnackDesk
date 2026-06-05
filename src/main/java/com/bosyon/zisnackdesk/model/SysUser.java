package com.bosyon.zisnackdesk.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @Id
    private String id;

    private String account;

    private String mobile;

    private String email;

    private String password;

    private String userType = "member";

    private Boolean mobileVerified = false;

    private Boolean emailVerified = false;

    private LocalDateTime deletedAt;

    private String createdBy;

    private String updatedBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
