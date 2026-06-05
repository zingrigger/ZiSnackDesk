package com.bosyon.zisnackdesk.model.dto;

import lombok.Data;

@Data
public class SysUserUpdateDTO {

    private String id;

    private String account;

    private String mobile;

    private String email;

    private String password;

    private String userType;

    private Boolean mobileVerified;

    private Boolean emailVerified;

}
