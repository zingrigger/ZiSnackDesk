package com.bosyon.zisnackdesk.validation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bosyon.zisnackdesk.config.SpringContextHolder;
import com.bosyon.zisnackdesk.mapper.SysUserMapper;
import com.bosyon.zisnackdesk.user.infrastructure.po.SysUserPO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class NotConflictAccountValidator implements ConstraintValidator<NotConflictAccount, String> {

    private SysUserMapper sysUserMapper;

    @Override
    public void initialize(NotConflictAccount constraintAnnotation) {
        this.sysUserMapper = SpringContextHolder.getBean(SysUserMapper.class);
    }

    @Override
    public boolean isValid(String account, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(account)) {
            return true;
        }

        LambdaQueryWrapper<SysUserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserPO::getAccount, account).isNull(SysUserPO::getDeletedAt);
        Long count = sysUserMapper.selectCount(wrapper);
        return count == null || count == 0;
    }
}
