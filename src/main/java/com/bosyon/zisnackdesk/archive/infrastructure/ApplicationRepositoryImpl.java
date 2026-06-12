package com.bosyon.zisnackdesk.archive.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.Application;
import com.bosyon.zisnackdesk.archive.domain.ApplicationRepository;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ApplicationPO;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationQueryRequest;
import com.bosyon.zisnackdesk.mapper.ApplicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepository {

    private final ApplicationMapper applicationMapper;

    @Override
    public Application save(Application application) {
        ApplicationPO po = ApplicationConverter.toPO(application);
        applicationMapper.insert(po);
        application.setId(po.getId());
        application.setCreatedAt(po.getCreatedAt());
        return application;
    }

    @Override
    public Application update(Application application) {
        ApplicationPO po = ApplicationConverter.toPO(application);
        applicationMapper.updateById(po);
        return application;
    }

    @Override
    public Optional<Application> findById(Long id) {
        return Optional.ofNullable(applicationMapper.selectById(id))
                .map(ApplicationConverter::toDomain);
    }

    @Override
    public IPage<Application> query(ApplicationQueryRequest query, int pageNum, int pageSize) {
        LambdaQueryWrapper<ApplicationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(ApplicationPO::getDeletedAt);

        if (query.applicantId() != null) {
            wrapper.eq(ApplicationPO::getApplicantId, query.applicantId());
        }
        if (query.status() != null) {
            wrapper.eq(ApplicationPO::getStatus, query.status());
        }

        wrapper.orderByDesc(ApplicationPO::getCreatedAt);

        IPage<ApplicationPO> poPage = applicationMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return poPage.convert(ApplicationConverter::toDomain);
    }

    @Override
    public boolean softDelete(Long id) {
        ApplicationPO po = new ApplicationPO();
        po.setId(id);
        po.setDeletedAt(LocalDateTime.now());
        return applicationMapper.updateById(po) > 0;
    }

    @Override
    public void softDeleteBatch(List<Long> ids) {
        ids.forEach(this::softDelete);
    }
}
