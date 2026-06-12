package com.bosyon.zisnackdesk.user.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bosyon.zisnackdesk.user.interfaces.dto.SysUserQueryRequest;

import java.util.List;
import java.util.Optional;

public interface SysUserRepository {
    SysUser save(SysUser user);
    SysUser update(SysUser user);
    Optional<SysUser> findById(String id);
    IPage<SysUser> query(SysUserQueryRequest query, int pageNum, int pageSize);
    boolean softDelete(String id);
    void softDeleteBatch(List<String> ids);
    boolean existsByAccount(String account);
}
