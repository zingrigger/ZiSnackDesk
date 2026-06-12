package com.bosyon.zisnackdesk.user.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.user.domain.SysUser;
import com.bosyon.zisnackdesk.user.domain.SysUserRepository;
import com.bosyon.zisnackdesk.user.interfaces.dto.SysUserCreateRequest;
import com.bosyon.zisnackdesk.user.interfaces.dto.SysUserQueryRequest;
import com.bosyon.zisnackdesk.user.interfaces.dto.SysUserResponse;
import com.bosyon.zisnackdesk.user.interfaces.dto.SysUserUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SysUserApplicationService {

    private final SysUserRepository sysUserRepository;

    public SysUserResponse createUser(SysUserCreateRequest request) {
        if (sysUserRepository.existsByAccount(request.account())) {
            throw new RuntimeException("账号已存在: " + request.account());
        }
        SysUser user = new SysUser(request.account(), request.password(), request.userType());
        user.setMobile(request.mobile());
        user.setEmail(request.email());
        user.validateForCreate();
        SysUser saved = sysUserRepository.save(user);
        log.info("创建用户成功, id: {}, account: {}", saved.getId(), saved.getAccount());
        return saved.toVO();
    }

    public SysUserResponse updateUser(SysUserUpdateRequest request) {
        SysUser user = sysUserRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("用户不存在, id: " + request.id()));
        user.setAccount(request.account());
        user.setMobile(request.mobile());
        user.setEmail(request.email());
        if (request.password() != null) {
            user.setPassword(request.password());
        }
        user.setUserType(request.userType());
        user.setMobileVerified(request.mobileVerified());
        user.setEmailVerified(request.emailVerified());
        user.validateForUpdate();
        SysUser saved = sysUserRepository.update(user);
        log.info("更新用户成功, id: {}", saved.getId());
        return saved.toVO();
    }

    public SysUserResponse getUserById(String id) {
        return sysUserRepository.findById(id)
                .map(SysUser::toVO)
                .orElse(null);
    }

    public IPage<SysUserResponse> queryUsers(SysUserQueryRequest request, int pageNum, int pageSize) {
        return sysUserRepository.query(request, pageNum, pageSize)
                .convert(SysUser::toVO);
    }

    public boolean deleteUser(String id) {
        return sysUserRepository.findById(id)
                .map(user -> {
                    user.markAsDeleted();
                    sysUserRepository.update(user);
                    log.info("软删除用户成功, id: {}", id);
                    return true;
                })
                .orElse(false);
    }

    public boolean batchDeleteUsers(List<String> ids) {
        sysUserRepository.softDeleteBatch(ids);
        log.info("批量软删除用户成功, ids: {}", ids);
        return true;
    }
}
