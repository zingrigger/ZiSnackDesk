package com.bosyon.zisnackdesk.archive.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.ApplicationDetail;
import com.bosyon.zisnackdesk.archive.domain.ApplicationDetailRepository;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ApplicationDetailPO;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailQueryRequest;
import com.bosyon.zisnackdesk.mapper.ApplicationDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApplicationDetailRepositoryImpl implements ApplicationDetailRepository {

    private final ApplicationDetailMapper applicationDetailMapper;

    @Override
    public ApplicationDetail save(ApplicationDetail detail) {
        ApplicationDetailPO po = ApplicationDetailConverter.toPO(detail);
        applicationDetailMapper.insert(po);
        detail.setId(po.getId());
        detail.setCreatedAt(po.getCreatedAt());
        return detail;
    }

    @Override
    public ApplicationDetail update(ApplicationDetail detail) {
        ApplicationDetailPO po = ApplicationDetailConverter.toPO(detail);
        applicationDetailMapper.updateById(po);
        return detail;
    }

    @Override
    public Optional<ApplicationDetail> findById(Long id) {
        return Optional.ofNullable(applicationDetailMapper.selectById(id))
                .map(ApplicationDetailConverter::toDomain);
    }

    @Override
    public IPage<ApplicationDetail> query(ApplicationDetailQueryRequest query, int pageNum, int pageSize) {
        LambdaQueryWrapper<ApplicationDetailPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(ApplicationDetailPO::getDeletedAt);

        if (query.applicationId() != null) {
            wrapper.eq(ApplicationDetailPO::getApplicationId, query.applicationId());
        }
        if (query.archiveId() != null) {
            wrapper.eq(ApplicationDetailPO::getArchiveId, query.archiveId());
        }

        wrapper.orderByDesc(ApplicationDetailPO::getCreatedAt);

        IPage<ApplicationDetailPO> poPage = applicationDetailMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return poPage.convert(ApplicationDetailConverter::toDomain);
    }

    @Override
    public boolean softDelete(Long id) {
        ApplicationDetailPO po = new ApplicationDetailPO();
        po.setId(id);
        po.setDeletedAt(LocalDateTime.now());
        return applicationDetailMapper.updateById(po) > 0;
    }

    @Override
    public void softDeleteBatch(List<Long> ids) {
        ids.forEach(this::softDelete);
    }
}
