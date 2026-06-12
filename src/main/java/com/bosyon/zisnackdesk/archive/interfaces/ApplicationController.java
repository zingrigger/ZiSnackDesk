package com.bosyon.zisnackdesk.archive.interfaces;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bosyon.zisnackdesk.archive.application.ApplicationApplicationService;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ApplicationController {

    private final ApplicationApplicationService applicationService;

    @PostMapping
    public ApplicationResponse createApplication(@Valid @RequestBody ApplicationCreateRequest request) {
        return applicationService.createApplication(request);
    }

    @PutMapping
    public ApplicationResponse updateApplication(@Valid @RequestBody ApplicationUpdateRequest request) {
        return applicationService.updateApplication(request);
    }

    @GetMapping("/{id}")
    public ApplicationResponse getApplicationById(@PathVariable @NotNull Long id) {
        return applicationService.getApplicationById(id);
    }

    @GetMapping("/list")
    public IPage<ApplicationResponse> queryApplications(ApplicationQueryRequest request,
                                                        @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        return applicationService.queryApplications(request, pageNum, pageSize);
    }

    @DeleteMapping("/{id}")
    public boolean deleteApplication(@PathVariable @NotNull Long id) {
        return applicationService.deleteApplication(id);
    }

    @PostMapping("/batch-delete")
    public boolean batchDeleteApplications(@RequestBody @NotEmpty List<Long> ids) {
        return applicationService.batchDeleteApplications(ids);
    }
}
