package com.bosyon.zisnackdesk.archive.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.ApplicationDetail;
import com.bosyon.zisnackdesk.archive.domain.ApplicationDetailRepository;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationDetailApplicationService {

    private final ApplicationDetailRepository applicationDetailRepository;

    public ApplicationDetailResponse createDetail(ApplicationDetailCreateRequest request) {
        ApplicationDetail detail = new ApplicationDetail(request.applicationId(), request.archiveId());
        detail.validateForCreate();
        ApplicationDetail saved = applicationDetailRepository.save(detail);
        log.info("创建申请单明细成功, id: {}", saved.getId());
        return saved.toVO();
    }

    public ApplicationDetailResponse updateDetail(ApplicationDetailUpdateRequest request) {
        ApplicationDetail detail = applicationDetailRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("申请单明细不存在, id: " + request.id()));
        detail.setApplicationId(request.applicationId());
        detail.setArchiveId(request.archiveId());
        detail.validateForUpdate();
        ApplicationDetail saved = applicationDetailRepository.update(detail);
        log.info("更新申请单明细成功, id: {}", saved.getId());
        return saved.toVO();
    }

    public ApplicationDetailResponse getDetailById(Long id) {
        return applicationDetailRepository.findById(id)
                .map(ApplicationDetail::toVO)
                .orElse(null);
    }

    public IPage<ApplicationDetailResponse> queryDetails(ApplicationDetailQueryRequest request, int pageNum, int pageSize) {
        return applicationDetailRepository.query(request, pageNum, pageSize)
                .convert(ApplicationDetail::toVO);
    }

    public boolean deleteDetail(Long id) {
        return applicationDetailRepository.findById(id)
                .map(detail -> {
                    detail.markAsDeleted();
                    applicationDetailRepository.update(detail);
                    log.info("软删除申请单明细成功, id: {}", id);
                    return true;
                })
                .orElse(false);
    }

    public boolean batchDeleteDetails(List<Long> ids) {
        applicationDetailRepository.softDeleteBatch(ids);
        log.info("批量软删除申请单明细成功, ids: {}", ids);
        return true;
    }
}
