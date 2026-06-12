package com.bosyon.zisnackdesk.archive.interfaces;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bosyon.zisnackdesk.archive.application.ArchiveApplicationService;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/archive")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ArchiveController {

    private final ArchiveApplicationService archiveService;

    @PostMapping
    public ArchiveResponse createArchive(@Valid @RequestBody ArchiveCreateRequest request) {
        return archiveService.createArchive(request);
    }

    @PutMapping
    public ArchiveResponse updateArchive(@Valid @RequestBody ArchiveUpdateRequest request) {
        return archiveService.updateArchive(request);
    }

    @GetMapping("/{id}")
    public ArchiveResponse getArchiveById(@PathVariable @NotNull Long id) {
        return archiveService.getArchiveById(id);
    }

    @GetMapping("/list")
    public IPage<ArchiveResponse> queryArchives(ArchiveQueryRequest request,
                                                @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                                @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        return archiveService.queryArchives(request, pageNum, pageSize);
    }

    @DeleteMapping("/{id}")
    public boolean deleteArchive(@PathVariable @NotNull Long id) {
        return archiveService.deleteArchive(id);
    }

    @PostMapping("/batch-delete")
    public boolean batchDeleteArchives(@RequestBody @NotEmpty List<Long> ids) {
        return archiveService.batchDeleteArchives(ids);
    }
}
