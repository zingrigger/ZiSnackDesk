package com.bosyon.zisnackdesk.archive.interfaces.dto;

import jakarta.validation.constraints.NotNull;

public record ArchiveUpdateRequest(
        @NotNull(message = "id 不能为空")
        Long id,
        Integer status,
        Long currentApplicationId
) {}
