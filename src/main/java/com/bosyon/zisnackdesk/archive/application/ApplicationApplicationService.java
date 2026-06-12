package com.bosyon.zisnackdesk.archive.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.Application;
import com.bosyon.zisnackdesk.archive.domain.ApplicationRepository;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationResponse createApplication(ApplicationCreateRequest request) {
        Application app = new Application(request.applicantId(), request.status());
        app.validateForCreate();
        Application saved = applicationRepository.save(app);
        log.info("创建申请单成功, id: {}", saved.getId());
        return saved.toVO();
    }

    public ApplicationResponse updateApplication(ApplicationUpdateRequest request) {
        Application app = applicationRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("申请单不存在, id: " + request.id()));
        app.setApplicantId(request.applicantId());
        app.setStatus(request.status());
        app.validateForUpdate();
        Application saved = applicationRepository.update(app);
        log.info("更新申请单成功, id: {}", saved.getId());
        return saved.toVO();
    }

    public ApplicationResponse getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .map(Application::toVO)
                .orElse(null);
    }

    public IPage<ApplicationResponse> queryApplications(ApplicationQueryRequest request, int pageNum, int pageSize) {
        return applicationRepository.query(request, pageNum, pageSize)
                .convert(Application::toVO);
    }

    public boolean deleteApplication(Long id) {
        return applicationRepository.findById(id)
                .map(app -> {
                    app.markAsDeleted();
                    applicationRepository.update(app);
                    log.info("软删除申请单成功, id: {}", id);
                    return true;
                })
                .orElse(false);
    }

    public boolean batchDeleteApplications(List<Long> ids) {
        applicationRepository.softDeleteBatch(ids);
        log.info("批量软删除申请单成功, ids: {}", ids);
        return true;
    }
}
