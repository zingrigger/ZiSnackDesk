package com.bosyon.zisnackdesk.archive.interfaces.dto;

public record ApplicationDetailQueryRequest(
        Long applicationId,
        Long archiveId
) {}
