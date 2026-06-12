package com.bosyon.zisnackdesk.archive.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArchiveResponse {
    private Long id;
    private Integer status;
    private Long currentApplicationId;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
