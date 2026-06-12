package com.bosyon.zisnackdesk.archive.interfaces.dto;

import jakarta.validation.constraints.NotNull;

public record ApplicationUpdateRequest(
        @NotNull(message = "id 不能为空")
        Long id,
        Long applicantId,
        Integer status
) {}
