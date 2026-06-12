package com.bosyon.zisnackdesk.user.interfaces.dto;

public record SysUserQueryRequest(
        String account,
        String mobile,
        String email,
        String userType,
        Boolean mobileVerified,
        Boolean emailVerified
) {}
