package com.bosyon.zisnackdesk.archive.infrastructure;

import com.bosyon.zisnackdesk.archive.domain.Archive;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ArchivePO;

public class ArchiveConverter {

    public static ArchivePO toPO(Archive domain) {
        if (domain == null) return null;
        ArchivePO po = new ArchivePO();
        po.setId(domain.getId());
        po.setStatus(domain.getStatus());
        po.setCurrentApplicationId(domain.getCurrentApplicationId());
        po.setDeletedAt(domain.getDeletedAt());
        po.setCreatedBy(domain.getCreatedBy());
        po.setUpdatedBy(domain.getUpdatedBy());
        po.setCreatedAt(domain.getCreatedAt());
        po.setUpdatedAt(domain.getUpdatedAt());
        return po;
    }

    public static Archive toDomain(ArchivePO po) {
        if (po == null) return null;
        Archive domain = new Archive();
        domain.setId(po.getId());
        domain.setStatus(po.getStatus());
        domain.setCurrentApplicationId(po.getCurrentApplicationId());
        domain.setDeletedAt(po.getDeletedAt());
        domain.setCreatedBy(po.getCreatedBy());
        domain.setUpdatedBy(po.getUpdatedBy());
        domain.setCreatedAt(po.getCreatedAt());
        domain.setUpdatedAt(po.getUpdatedAt());
        return domain;
    }
}
