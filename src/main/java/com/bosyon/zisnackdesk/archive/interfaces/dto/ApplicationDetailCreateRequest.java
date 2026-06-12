package com.bosyon.zisnackdesk.archive.interfaces.dto;

public record ApplicationDetailCreateRequest(
        Long applicationId,
        Long archiveId
) {}
