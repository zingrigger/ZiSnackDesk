package com.bosyon.zisnackdesk.archive.interfaces.dto;

public record ArchiveQueryRequest(
        Integer status,
        Long currentApplicationId
) {}
