package com.bosyon.zisnackdesk.user.interfaces.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SysUserUpdateRequest(
        @NotBlank(message = "id 不能为空")
        String id,

        String account,

        String mobile,

        @Email(message = "email 格式不正确")
        String email,

        @Size(min = 6, max = 128, message = "password 长度应在 6 到 128 之间")
        String password,

        String userType,

        Boolean mobileVerified,

        Boolean emailVerified
) {}
