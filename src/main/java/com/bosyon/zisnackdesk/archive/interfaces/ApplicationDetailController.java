package com.bosyon.zisnackdesk.archive.interfaces;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bosyon.zisnackdesk.archive.application.ApplicationDetailApplicationService;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application-detail")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ApplicationDetailController {

    private final ApplicationDetailApplicationService applicationDetailService;

    @PostMapping
    public ApplicationDetailResponse createDetail(@Valid @RequestBody ApplicationDetailCreateRequest request) {
        return applicationDetailService.createDetail(request);
    }

    @PutMapping
    public ApplicationDetailResponse updateDetail(@Valid @RequestBody ApplicationDetailUpdateRequest request) {
        return applicationDetailService.updateDetail(request);
    }

    @GetMapping("/{id}")
    public ApplicationDetailResponse getDetailById(@PathVariable @NotNull Long id) {
        return applicationDetailService.getDetailById(id);
    }

    @GetMapping("/list")
    public IPage<ApplicationDetailResponse> queryDetails(ApplicationDetailQueryRequest request,
                                                         @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        return applicationDetailService.queryDetails(request, pageNum, pageSize);
    }

    @DeleteMapping("/{id}")
    public boolean deleteDetail(@PathVariable @NotNull Long id) {
        return applicationDetailService.deleteDetail(id);
    }

    @PostMapping("/batch-delete")
    public boolean batchDeleteDetails(@RequestBody @NotEmpty List<Long> ids) {
        return applicationDetailService.batchDeleteDetails(ids);
    }
}
