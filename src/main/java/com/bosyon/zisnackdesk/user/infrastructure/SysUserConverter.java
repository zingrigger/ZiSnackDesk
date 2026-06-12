package com.bosyon.zisnackdesk.user.infrastructure;

import com.bosyon.zisnackdesk.user.domain.SysUser;
import com.bosyon.zisnackdesk.user.infrastructure.po.SysUserPO;

public class SysUserConverter {

    public static SysUserPO toPO(SysUser domain) {
        if (domain == null) return null;
        SysUserPO po = new SysUserPO();
        po.setId(domain.getId());
        po.setAccount(domain.getAccount());
        po.setMobile(domain.getMobile());
        po.setEmail(domain.getEmail());
        po.setPassword(domain.getPassword());
        po.setUserType(domain.getUserType());
        po.setMobileVerified(domain.getMobileVerified());
        po.setEmailVerified(domain.getEmailVerified());
        po.setDeletedAt(domain.getDeletedAt());
        po.setCreatedBy(domain.getCreatedBy());
        po.setUpdatedBy(domain.getUpdatedBy());
        po.setCreatedAt(domain.getCreatedAt());
        po.setUpdatedAt(domain.getUpdatedAt());
        return po;
    }

    public static SysUser toDomain(SysUserPO po) {
        if (po == null) return null;
        SysUser domain = new SysUser();
        domain.setId(po.getId());
        domain.setAccount(po.getAccount());
        domain.setMobile(po.getMobile());
        domain.setEmail(po.getEmail());
        domain.setPassword(po.getPassword());
        domain.setUserType(po.getUserType());
        domain.setMobileVerified(po.getMobileVerified());
        domain.setEmailVerified(po.getEmailVerified());
        domain.setDeletedAt(po.getDeletedAt());
        domain.setCreatedBy(po.getCreatedBy());
        domain.setUpdatedBy(po.getUpdatedBy());
        domain.setCreatedAt(po.getCreatedAt());
        domain.setUpdatedAt(po.getUpdatedAt());
        return domain;
    }
}
