package com.bosyon.zisnackdesk.archive.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationQueryRequest;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {
    Application save(Application application);
    Application update(Application application);
    Optional<Application> findById(Long id);
    IPage<Application> query(ApplicationQueryRequest query, int pageNum, int pageSize);
    boolean softDelete(Long id);
    void softDeleteBatch(List<Long> ids);
}
