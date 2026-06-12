package com.bosyon.zisnackdesk.archive.infrastructure;

import com.bosyon.zisnackdesk.archive.domain.Application;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ApplicationPO;

public class ApplicationConverter {

    public static ApplicationPO toPO(Application domain) {
        if (domain == null) return null;
        ApplicationPO po = new ApplicationPO();
        po.setId(domain.getId());
        po.setApplicantId(domain.getApplicantId());
        po.setStatus(domain.getStatus());
        po.setCreatedAt(domain.getCreatedAt());
        po.setDeletedAt(domain.getDeletedAt());
        po.setCreatedBy(domain.getCreatedBy());
        po.setUpdatedBy(domain.getUpdatedBy());
        po.setUpdatedAt(domain.getUpdatedAt());
        return po;
    }

    public static Application toDomain(ApplicationPO po) {
        if (po == null) return null;
        Application domain = new Application();
        domain.setId(po.getId());
        domain.setApplicantId(po.getApplicantId());
        domain.setStatus(po.getStatus());
        domain.setCreatedAt(po.getCreatedAt());
        domain.setDeletedAt(po.getDeletedAt());
        domain.setCreatedBy(po.getCreatedBy());
        domain.setUpdatedBy(po.getUpdatedBy());
        domain.setUpdatedAt(po.getUpdatedAt());
        return domain;
    }
}
