package com.bosyon.zisnackdesk.archive.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.Archive;
import com.bosyon.zisnackdesk.archive.domain.ArchiveRepository;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ArchivePO;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveQueryRequest;
import com.bosyon.zisnackdesk.mapper.ArchiveMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArchiveRepositoryImpl implements ArchiveRepository {

    private final ArchiveMapper archiveMapper;

    @Override
    public Archive save(Archive archive) {
        ArchivePO po = ArchiveConverter.toPO(archive);
        archiveMapper.insert(po);
        archive.setId(po.getId());
        archive.setCreatedAt(po.getCreatedAt());
        return archive;
    }

    @Override
    public Archive update(Archive archive) {
        ArchivePO po = ArchiveConverter.toPO(archive);
        archiveMapper.updateById(po);
        return archive;
    }

    @Override
    public Optional<Archive> findById(Long id) {
        return Optional.ofNullable(archiveMapper.selectById(id))
                .map(ArchiveConverter::toDomain);
    }

    @Override
    public IPage<Archive> query(ArchiveQueryRequest query, int pageNum, int pageSize) {
        LambdaQueryWrapper<ArchivePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(ArchivePO::getDeletedAt);

        if (query.status() != null) {
            wrapper.eq(ArchivePO::getStatus, query.status());
        }
        if (query.currentApplicationId() != null) {
            wrapper.eq(ArchivePO::getCurrentApplicationId, query.currentApplicationId());
        }

        wrapper.orderByDesc(ArchivePO::getCreatedAt);

        IPage<ArchivePO> poPage = archiveMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return poPage.convert(ArchiveConverter::toDomain);
    }

    @Override
    public boolean softDelete(Long id) {
        ArchivePO po = new ArchivePO();
        po.setId(id);
        po.setDeletedAt(LocalDateTime.now());
        return archiveMapper.updateById(po) > 0;
    }

    @Override
    public void softDeleteBatch(List<Long> ids) {
        ids.forEach(this::softDelete);
    }
}
