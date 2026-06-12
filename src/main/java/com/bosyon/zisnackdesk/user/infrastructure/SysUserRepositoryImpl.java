package com.bosyon.zisnackdesk.user.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.mapper.SysUserMapper;
import com.bosyon.zisnackdesk.user.domain.SysUser;
import com.bosyon.zisnackdesk.user.domain.SysUserRepository;
import com.bosyon.zisnackdesk.user.infrastructure.po.SysUserPO;
import com.bosyon.zisnackdesk.user.interfaces.dto.SysUserQueryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SysUserRepositoryImpl implements SysUserRepository {

    private final SysUserMapper sysUserMapper;

    @Override
    public SysUser save(SysUser user) {
        SysUserPO po = SysUserConverter.toPO(user);
        sysUserMapper.insert(po);
        user.setId(po.getId());
        user.setCreatedAt(po.getCreatedAt());
        return user;
    }

    @Override
    public SysUser update(SysUser user) {
        SysUserPO po = SysUserConverter.toPO(user);
        sysUserMapper.updateById(po);
        return user;
    }

    @Override
    public Optional<SysUser> findById(String id) {
        return Optional.ofNullable(sysUserMapper.selectById(id))
                .map(SysUserConverter::toDomain);
    }

    @Override
    public IPage<SysUser> query(SysUserQueryRequest query, int pageNum, int pageSize) {
        LambdaQueryWrapper<SysUserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(SysUserPO::getDeletedAt);

        if (StringUtils.hasText(query.account())) {
            wrapper.like(SysUserPO::getAccount, query.account());
        }
        if (StringUtils.hasText(query.mobile())) {
            wrapper.like(SysUserPO::getMobile, query.mobile());
        }
        if (StringUtils.hasText(query.email())) {
            wrapper.like(SysUserPO::getEmail, query.email());
        }
        if (StringUtils.hasText(query.userType())) {
            wrapper.eq(SysUserPO::getUserType, query.userType());
        }
        if (query.mobileVerified() != null) {
            wrapper.eq(SysUserPO::getMobileVerified, query.mobileVerified());
        }
        if (query.emailVerified() != null) {
            wrapper.eq(SysUserPO::getEmailVerified, query.emailVerified());
        }

        wrapper.orderByDesc(SysUserPO::getCreatedAt);

        IPage<SysUserPO> poPage = sysUserMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return poPage.convert(SysUserConverter::toDomain);
    }

    @Override
    public boolean softDelete(String id) {
        SysUserPO po = new SysUserPO();
        po.setId(id);
        po.setDeletedAt(LocalDateTime.now());
        return sysUserMapper.updateById(po) > 0;
    }

    @Override
    public void softDeleteBatch(List<String> ids) {
        ids.forEach(this::softDelete);
    }

    @Override
    public boolean existsByAccount(String account) {
        LambdaQueryWrapper<SysUserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserPO::getAccount, account).isNull(SysUserPO::getDeletedAt);
        return sysUserMapper.selectCount(wrapper) > 0;
    }
}
