package com.bosyon.zisnackdesk.archive.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveQueryRequest;

import java.util.List;
import java.util.Optional;

public interface ArchiveRepository {
    Archive save(Archive archive);
    Archive update(Archive archive);
    Optional<Archive> findById(Long id);
    IPage<Archive> query(ArchiveQueryRequest query, int pageNum, int pageSize);
    boolean softDelete(Long id);
    void softDeleteBatch(List<Long> ids);
}
