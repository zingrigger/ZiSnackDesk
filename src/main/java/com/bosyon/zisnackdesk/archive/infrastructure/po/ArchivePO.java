package com.bosyon.zisnackdesk.archive.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@TableName("archive")
public class ArchivePO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer status;

    @TableField("current_application_id")
    private Long currentApplicationId;

    private LocalDateTime deletedAt;

    private String createdBy;

    private String updatedBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
