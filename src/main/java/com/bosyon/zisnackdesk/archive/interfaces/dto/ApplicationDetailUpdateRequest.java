package com.bosyon.zisnackdesk.archive.interfaces.dto;

import jakarta.validation.constraints.NotNull;

public record ApplicationDetailUpdateRequest(
        @NotNull(message = "id 不能为空")
        Long id,
        Long applicationId,
        Long archiveId
) {}
