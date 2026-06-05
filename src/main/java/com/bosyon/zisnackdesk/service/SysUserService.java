package com.bosyon.zisnackdesk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bosyon.zisnackdesk.model.SysUser;
import com.bosyon.zisnackdesk.model.dto.SysUserCreateDTO;
import com.bosyon.zisnackdesk.model.dto.SysUserQueryDTO;
import com.bosyon.zisnackdesk.model.dto.SysUserUpdateDTO;
import com.bosyon.zisnackdesk.model.vo.SysUserVO;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

    /**
     * 创建用户
     */
    SysUserVO createUser(SysUserCreateDTO createDTO);

    /**
     * 更新用户
     */
    SysUserVO updateUser(SysUserUpdateDTO updateDTO);

    /**
     * 根据 ID 获取用户 VO
     */
    SysUserVO getUserVOById(String id);

    /**
     * 条件分页查询用户
     */
    IPage<SysUserVO> queryUsers(SysUserQueryDTO queryDTO, int pageNum, int pageSize);

    /**
     * 软删除用户（设置 deletedAt）
     */
    boolean deleteUser(String id);

    /**
     * 批量软删除用户
     */
    boolean batchDeleteUsers(List<String> ids);
}
