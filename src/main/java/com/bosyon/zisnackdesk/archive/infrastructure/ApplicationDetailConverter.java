package com.bosyon.zisnackdesk.archive.infrastructure;

import com.bosyon.zisnackdesk.archive.domain.ApplicationDetail;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ApplicationDetailPO;

public class ApplicationDetailConverter {

    public static ApplicationDetailPO toPO(ApplicationDetail domain) {
        if (domain == null) return null;
        ApplicationDetailPO po = new ApplicationDetailPO();
        po.setId(domain.getId());
        po.setApplicationId(domain.getApplicationId());
        po.setArchiveId(domain.getArchiveId());
        po.setDeletedAt(domain.getDeletedAt());
        po.setCreatedBy(domain.getCreatedBy());
        po.setUpdatedBy(domain.getUpdatedBy());
        po.setCreatedAt(domain.getCreatedAt());
        po.setUpdatedAt(domain.getUpdatedAt());
        return po;
    }

    public static ApplicationDetail toDomain(ApplicationDetailPO po) {
        if (po == null) return null;
        ApplicationDetail domain = new ApplicationDetail();
        domain.setId(po.getId());
        domain.setApplicationId(po.getApplicationId());
        domain.setArchiveId(po.getArchiveId());
        domain.setDeletedAt(po.getDeletedAt());
        domain.setCreatedBy(po.getCreatedBy());
        domain.setUpdatedBy(po.getUpdatedBy());
        domain.setCreatedAt(po.getCreatedAt());
        domain.setUpdatedAt(po.getUpdatedAt());
        return domain;
    }
}
