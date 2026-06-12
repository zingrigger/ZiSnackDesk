package com.bosyon.zisnackdesk.user.interfaces;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bosyon.zisnackdesk.user.application.SysUserApplicationService;
import com.bosyon.zisnackdesk.user.interfaces.dto.SysUserCreateRequest;
import com.bosyon.zisnackdesk.user.interfaces.dto.SysUserQueryRequest;
import com.bosyon.zisnackdesk.user.interfaces.dto.SysUserResponse;
import com.bosyon.zisnackdesk.user.interfaces.dto.SysUserUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SysUserController {

    private final SysUserApplicationService sysUserService;

    @PostMapping
    public SysUserResponse createUser(@Valid @RequestBody SysUserCreateRequest request) {
        return sysUserService.createUser(request);
    }

    @PutMapping
    public SysUserResponse updateUser(@Valid @RequestBody SysUserUpdateRequest request) {
        return sysUserService.updateUser(request);
    }

    @GetMapping("/{id}")
    public SysUserResponse getUserById(@PathVariable @NotBlank String id) {
        return sysUserService.getUserById(id);
    }

    @GetMapping("/list")
    public IPage<SysUserResponse> queryUsers(SysUserQueryRequest request,
                                             @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                             @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        return sysUserService.queryUsers(request, pageNum, pageSize);
    }

    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable @NotBlank String id) {
        return sysUserService.deleteUser(id);
    }

    @PostMapping("/batch-delete")
    public boolean batchDeleteUsers(@RequestBody @NotEmpty List<String> ids) {
        return sysUserService.batchDeleteUsers(ids);
    }
}
