package com.bosyon.zisnackdesk.archive.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.Archive;
import com.bosyon.zisnackdesk.archive.domain.ArchiveRepository;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ArchiveApplicationService {

    private final ArchiveRepository archiveRepository;

    public ArchiveResponse createArchive(ArchiveCreateRequest request) {
        Archive archive = new Archive(request.status(), request.currentApplicationId());
        archive.validateForCreate();
        Archive saved = archiveRepository.save(archive);
        log.info("创建档案成功, id: {}", saved.getId());
        return saved.toVO();
    }

    public ArchiveResponse updateArchive(ArchiveUpdateRequest request) {
        Archive archive = archiveRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("档案不存在, id: " + request.id()));
        archive.setStatus(request.status());
        archive.setCurrentApplicationId(request.currentApplicationId());
        archive.validateForUpdate();
        Archive saved = archiveRepository.update(archive);
        log.info("更新档案成功, id: {}", saved.getId());
        return saved.toVO();
    }

    public ArchiveResponse getArchiveById(Long id) {
        return archiveRepository.findById(id)
                .map(Archive::toVO)
                .orElse(null);
    }

    public IPage<ArchiveResponse> queryArchives(ArchiveQueryRequest request, int pageNum, int pageSize) {
        return archiveRepository.query(request, pageNum, pageSize)
                .convert(Archive::toVO);
    }

    public boolean deleteArchive(Long id) {
        return archiveRepository.findById(id)
                .map(archive -> {
                    archive.markAsDeleted();
                    archiveRepository.update(archive);
                    log.info("软删除档案成功, id: {}", id);
                    return true;
                })
                .orElse(false);
    }

    public boolean batchDeleteArchives(List<Long> ids) {
        archiveRepository.softDeleteBatch(ids);
        log.info("批量软删除档案成功, ids: {}", ids);
        return true;
    }
}
