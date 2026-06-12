package com.bosyon.zisnackdesk.archive.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailQueryRequest;

import java.util.List;
import java.util.Optional;

public interface ApplicationDetailRepository {
    ApplicationDetail save(ApplicationDetail detail);
    ApplicationDetail update(ApplicationDetail detail);
    Optional<ApplicationDetail> findById(Long id);
    IPage<ApplicationDetail> query(ApplicationDetailQueryRequest query, int pageNum, int pageSize);
    boolean softDelete(Long id);
    void softDeleteBatch(List<Long> ids);
}
