package com.bosyon.zisnackdesk.archive.interfaces.dto;

public record ArchiveCreateRequest(
        Integer status,
        Long currentApplicationId
) {}
