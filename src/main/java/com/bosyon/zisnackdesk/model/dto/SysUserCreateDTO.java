package com.bosyon.zisnackdesk.model.dto;

import lombok.Data;

@Data
public class SysUserCreateDTO {

    private String account;

    private String mobile;

    private String email;

    private String password;

    private String userType = "member";

}
