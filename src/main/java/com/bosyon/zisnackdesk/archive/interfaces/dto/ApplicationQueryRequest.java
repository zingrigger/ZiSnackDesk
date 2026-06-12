package com.bosyon.zisnackdesk.archive.interfaces.dto;

public record ApplicationQueryRequest(
        Long applicantId,
        Integer status
) {}
