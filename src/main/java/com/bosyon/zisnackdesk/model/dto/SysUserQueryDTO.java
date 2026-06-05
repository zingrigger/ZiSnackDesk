package com.bosyon.zisnackdesk.model.dto;

import lombok.Data;

@Data
public class SysUserQueryDTO {

    private String account;

    private String mobile;

    private String email;

    private String userType;

    private Boolean mobileVerified;

    private Boolean emailVerified;

}
