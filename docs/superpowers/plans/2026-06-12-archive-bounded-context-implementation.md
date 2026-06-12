# 档案界限上下文实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 Archive、Application、ApplicationDetail 三个实体从贫血模型改造为半血模型，划分到 `archive` 界限上下文包中。

**Architecture:** 按 DDD 轻量分层（interfaces/application/domain/infrastructure），每个实体持自有校验和行为，PO 与 Domain 分离，Repository 封装 MyBatis-Plus，ApplicationService 只做编排。

**Tech Stack:** Java 21, Spring Boot 4, MyBatis-Plus 3.5, PostgreSQL, Lombok

**改造顺序（按依赖关系）：**
- Task 1-3: 基础设施 — PO + Mapper（无依赖）
- Task 4-6: Domain 实体（依赖 PO 结构先确定）
- Task 7-9: Repository 接口（依赖 Domain 实体）
- Task 10-12: Repository 实现（依赖 PO + Domain + Repository 接口）
- Task 13-15: ApplicationService（依赖 Repository 接口）
- Task 16-18: DTO/VO（独立，可与前序并行）
- Task 19-21: Controller 迁移（依赖 ApplicationService）
- Task 22-24: 清理旧代码

---

### Task 1: ArchivePO + ArchiveMapper

**Files:**
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/infrastructure/po/ArchivePO.java`
- Modify: `src/main/java/com/bosyon/zisnackdesk/mapper/ArchiveMapper.java`

- [ ] **Step 1: 创建 ArchivePO**

```java
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
```

- [ ] **Step 2: 修改 ArchiveMapper 指向 ArchivePO**

将 `ArchiveMapper extends BaseMapper<Archive>` 改为 `BaseMapper<ArchivePO>`：

```java
package com.bosyon.zisnackdesk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ArchivePO;

public interface ArchiveMapper extends BaseMapper<ArchivePO> {

}
```

- [ ] **Step 3: 验证编译通过**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 2: ApplicationPO + ApplicationMapper

**Files:**
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/infrastructure/po/ApplicationPO.java`
- Modify: `src/main/java/com/bosyon/zisnackdesk/mapper/ApplicationMapper.java`

- [ ] **Step 1: 创建 ApplicationPO**

```java
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
@TableName("application")
public class ApplicationPO {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("applicant_id")
    private Long applicantId;

    private Integer status;

    @TableField("create_time")
    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    private String createdBy;

    private String updatedBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: 修改 ApplicationMapper 指向 ApplicationPO**

```java
package com.bosyon.zisnackdesk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ApplicationPO;

public interface ApplicationMapper extends BaseMapper<ApplicationPO> {

}
```

- [ ] **Step 3: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 3: ApplicationDetailPO + ApplicationDetailMapper

**Files:**
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/infrastructure/po/ApplicationDetailPO.java`
- Modify: `src/main/java/com/bosyon/zisnackdesk/mapper/ApplicationDetailMapper.java`

- [ ] **Step 1: 创建 ApplicationDetailPO**

```java
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
@TableName("application_detail")
public class ApplicationDetailPO {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("application_id")
    private Long applicationId;

    @TableField("archive_id")
    private Long archiveId;

    private LocalDateTime deletedAt;

    private String createdBy;

    private String updatedBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: 修改 ApplicationDetailMapper 指向 ApplicationDetailPO**

```java
package com.bosyon.zisnackdesk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ApplicationDetailPO;

public interface ApplicationDetailMapper extends BaseMapper<ApplicationDetailPO> {

}
```

- [ ] **Step 3: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 4: Archive Domain 实体

**File:** Create: `src/main/java/com/bosyon/zisnackdesk/archive/domain/Archive.java`

- [ ] **Step 1: 创建 Archive 领域实体（半血）**

```java
package com.bosyon.zisnackdesk.archive.domain;

import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveResponse;

import java.time.LocalDateTime;

public class Archive {

    private Long id;
    private Integer status;
    private Long currentApplicationId;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // --- Constructor ---

    public Archive() {}

    public Archive(Integer status, Long currentApplicationId) {
        this.status = status;
        this.currentApplicationId = currentApplicationId;
    }

    // --- Behavior ---

    /** 创建时自校验（不查库） */
    public void validateForCreate() {
        if (status == null || status < 0) {
            throw new IllegalArgumentException("状态值不合法");
        }
        if (currentApplicationId == null) {
            throw new IllegalArgumentException("关联申请单不能为空");
        }
    }

    /** 更新时自校验 */
    public void validateForUpdate() {
        if (id == null) {
            throw new IllegalArgumentException("ID 不能为空");
        }
    }

    /** 软删除 */
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    /** 从 DTO 填充字段 */
    public void applyCreate(Integer status, Long currentApplicationId) {
        this.status = status;
        this.currentApplicationId = currentApplicationId;
    }

    /** 转 VO */
    public ArchiveResponse toVO() {
        ArchiveResponse vo = new ArchiveResponse();
        vo.setId(this.id);
        vo.setStatus(this.status);
        vo.setCurrentApplicationId(this.currentApplicationId);
        vo.setCreatedBy(this.createdBy);
        vo.setUpdatedBy(this.updatedBy);
        vo.setCreatedAt(this.createdAt);
        vo.setUpdatedAt(this.updatedAt);
        return vo;
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Long getCurrentApplicationId() { return currentApplicationId; }
    public void setCurrentApplicationId(Long currentApplicationId) { this.currentApplicationId = currentApplicationId; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 5: Application Domain 实体 + ApplicationStatus 枚举

**Files:**
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/domain/ApplicationStatus.java`
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/domain/Application.java`

- [ ] **Step 1: 创建 ApplicationStatus 枚举**

```java
package com.bosyon.zisnackdesk.archive.domain;

public enum ApplicationStatus {
    DRAFT(0, "草稿"),
    SUBMITTED(1, "已提交"),
    APPROVED(2, "已通过"),
    REJECTED(3, "已驳回"),
    CANCELLED(4, "已撤销");

    private final int code;
    private final String label;

    ApplicationStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() { return code; }
    public String getLabel() { return label; }

    public static ApplicationStatus fromCode(int code) {
        for (ApplicationStatus s : values()) {
            if (s.code == code) return s;
        }
        throw new IllegalArgumentException("未知状态码: " + code);
    }
}
```

- [ ] **Step 2: 创建 Application 领域实体**

```java
package com.bosyon.zisnackdesk.archive.domain;

import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationResponse;

import java.time.LocalDateTime;

public class Application {

    private Long id;
    private Long applicantId;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime updatedAt;

    // --- Constructor ---

    public Application() {}

    public Application(Long applicantId, Integer status) {
        this.applicantId = applicantId;
        this.status = status;
    }

    // --- Behavior ---

    public void validateForCreate() {
        if (applicantId == null) {
            throw new IllegalArgumentException("申请人不能为空");
        }
        if (status == null) {
            throw new IllegalArgumentException("状态不能为空");
        }
    }

    public void validateForUpdate() {
        if (id == null) {
            throw new IllegalArgumentException("ID 不能为空");
        }
    }

    /** 提交申请（草稿 → 已提交） */
    public void submit() {
        if (status != ApplicationStatus.DRAFT.getCode()) {
            throw new IllegalStateException("只有草稿状态的申请单可以提交");
        }
        this.status = ApplicationStatus.SUBMITTED.getCode();
    }

    /** 通过申请（已提交 → 已通过） */
    public void approve() {
        if (status != ApplicationStatus.SUBMITTED.getCode()) {
            throw new IllegalStateException("只有已提交状态的申请单可以通过");
        }
        this.status = ApplicationStatus.APPROVED.getCode();
    }

    /** 驳回申请（已提交 → 已驳回） */
    public void reject() {
        if (status != ApplicationStatus.SUBMITTED.getCode()) {
            throw new IllegalStateException("只有已提交状态的申请单可以驳回");
        }
        this.status = ApplicationStatus.REJECTED.getCode();
    }

    /** 软删除 */
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    /** 从 DTO 填充字段 */
    public void applyCreate(Long applicantId, Integer status) {
        this.applicantId = applicantId;
        this.status = status;
    }

    /** 从 DTO 填充更新 */
    public void applyUpdate(Long applicantId, Integer status) {
        this.applicantId = applicantId;
        this.status = status;
    }

    /** 转 VO */
    public ApplicationResponse toVO() {
        ApplicationResponse vo = new ApplicationResponse();
        vo.setId(this.id);
        vo.setApplicantId(this.applicantId);
        vo.setStatus(this.status);
        vo.setCreatedAt(this.createdAt);
        vo.setCreatedBy(this.createdBy);
        vo.setUpdatedBy(this.updatedBy);
        vo.setUpdatedAt(this.updatedAt);
        return vo;
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getApplicantId() { return applicantId; }
    public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

- [ ] **Step 3: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 6: ApplicationDetail Domain 实体

**File:** Create: `src/main/java/com/bosyon/zisnackdesk/archive/domain/ApplicationDetail.java`

- [ ] **Step 1: 创建 ApplicationDetail 领域实体**

```java
package com.bosyon.zisnackdesk.archive.domain;

import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailResponse;

import java.time.LocalDateTime;

public class ApplicationDetail {

    private Long id;
    private Long applicationId;
    private Long archiveId;
    private LocalDateTime deletedAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- Constructor ---

    public ApplicationDetail() {}

    public ApplicationDetail(Long applicationId, Long archiveId) {
        this.applicationId = applicationId;
        this.archiveId = archiveId;
    }

    // --- Behavior ---

    public void validateForCreate() {
        if (applicationId == null) {
            throw new IllegalArgumentException("申请单 ID 不能为空");
        }
        if (archiveId == null) {
            throw new IllegalArgumentException("档案 ID 不能为空");
        }
    }

    public void validateForUpdate() {
        if (id == null) {
            throw new IllegalArgumentException("ID 不能为空");
        }
    }

    /** 软删除 */
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    /** 从 DTO 填充 */
    public void applyCreate(Long applicationId, Long archiveId) {
        this.applicationId = applicationId;
        this.archiveId = archiveId;
    }

    /** 转 VO */
    public ApplicationDetailResponse toVO() {
        ApplicationDetailResponse vo = new ApplicationDetailResponse();
        vo.setId(this.id);
        vo.setApplicationId(this.applicationId);
        vo.setArchiveId(this.archiveId);
        vo.setCreatedAt(this.createdAt);
        vo.setCreatedBy(this.createdBy);
        vo.setUpdatedBy(this.updatedBy);
        vo.setUpdatedAt(this.updatedAt);
        return vo;
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public Long getArchiveId() { return archiveId; }
    public void setArchiveId(Long archiveId) { this.archiveId = archiveId; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 7: ArchiveRepository 接口

**File:** Create: `src/main/java/com/bosyon/zisnackdesk/archive/domain/ArchiveRepository.java`

- [ ] **Step 1: 创建 ArchiveRepository 接口**

```java
package com.bosyon.zisnackdesk.archive.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveQueryRequest;

import java.util.List;
import java.util.Optional;

public interface ArchiveRepository {
    Archive save(Archive archive);
    Archive update(Archive archive);
    Optional<Archive> findById(Long id);
    IPage<Archive> query(ArchiveQueryRequest query, Page<?> page);
    boolean softDelete(Long id);
    void softDeleteBatch(List<Long> ids);
}
```

---

### Task 8: ApplicationRepository 接口

**File:** Create: `src/main/java/com/bosyon/zisnackdesk/archive/domain/ApplicationRepository.java`

- [ ] **Step 1: 创建 ApplicationRepository 接口**

```java
package com.bosyon.zisnackdesk.archive.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationQueryRequest;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {
    Application save(Application application);
    Application update(Application application);
    Optional<Application> findById(Long id);
    IPage<Application> query(ApplicationQueryRequest query, Page<?> page);
    boolean softDelete(Long id);
    void softDeleteBatch(List<Long> ids);
}
```

---

### Task 9: ApplicationDetailRepository 接口

**File:** Create: `src/main/java/com/bosyon/zisnackdesk/archive/domain/ApplicationDetailRepository.java`

- [ ] **Step 1: 创建 ApplicationDetailRepository 接口**

```java
package com.bosyon.zisnackdesk.archive.domain;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailQueryRequest;

import java.util.List;
import java.util.Optional;

public interface ApplicationDetailRepository {
    ApplicationDetail save(ApplicationDetail detail);
    ApplicationDetail update(ApplicationDetail detail);
    Optional<ApplicationDetail> findById(Long id);
    IPage<ApplicationDetail> query(ApplicationDetailQueryRequest query, Page<?> page);
    boolean softDelete(Long id);
    void softDeleteBatch(List<Long> ids);
}
```

---

### Task 10: ArchiveRepositoryImpl

**Files:**
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/infrastructure/ArchiveRepositoryImpl.java`
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/infrastructure/ArchiveConverter.java`

- [ ] **Step 1: 创建 ArchiveConverter（PO ↔ Domain 转换器）**

```java
package com.bosyon.zisnackdesk.archive.infrastructure;

import com.bosyon.zisnackdesk.archive.domain.Archive;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ArchivePO;

public class ArchiveConverter {

    public static ArchivePO toPO(Archive domain) {
        if (domain == null) return null;
        ArchivePO po = new ArchivePO();
        po.setId(domain.getId());
        po.setStatus(domain.getStatus());
        po.setCurrentApplicationId(domain.getCurrentApplicationId());
        po.setDeletedAt(domain.getDeletedAt());
        po.setCreatedBy(domain.getCreatedBy());
        po.setUpdatedBy(domain.getUpdatedBy());
        po.setCreatedAt(domain.getCreatedAt());
        po.setUpdatedAt(domain.getUpdatedAt());
        return po;
    }

    public static Archive toDomain(ArchivePO po) {
        if (po == null) return null;
        Archive domain = new Archive();
        domain.setId(po.getId());
        domain.setStatus(po.getStatus());
        domain.setCurrentApplicationId(po.getCurrentApplicationId());
        domain.setDeletedAt(po.getDeletedAt());
        domain.setCreatedBy(po.getCreatedBy());
        domain.setUpdatedBy(po.getUpdatedBy());
        domain.setCreatedAt(po.getCreatedAt());
        domain.setUpdatedAt(po.getUpdatedAt());
        return domain;
    }
}
```

- [ ] **Step 2: 创建 ArchiveRepositoryImpl**

```java
package com.bosyon.zisnackdesk.archive.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.Archive;
import com.bosyon.zisnackdesk.archive.domain.ArchiveRepository;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ArchivePO;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveQueryRequest;
import com.bosyon.zisnackdesk.mapper.ArchiveMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArchiveRepositoryImpl implements ArchiveRepository {

    private final ArchiveMapper archiveMapper;

    @Override
    public Archive save(Archive archive) {
        ArchivePO po = ArchiveConverter.toPO(archive);
        archiveMapper.insert(po);
        archive.setId(po.getId());
        archive.setCreatedAt(po.getCreatedAt());
        return archive;
    }

    @Override
    public Archive update(Archive archive) {
        ArchivePO po = ArchiveConverter.toPO(archive);
        archiveMapper.updateById(po);
        return archive;
    }

    @Override
    public Optional<Archive> findById(Long id) {
        return Optional.ofNullable(archiveMapper.selectById(id))
                .map(ArchiveConverter::toDomain);
    }

    @Override
    public IPage<Archive> query(ArchiveQueryRequest query, Page<?> page) {
        LambdaQueryWrapper<ArchivePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(ArchivePO::getDeletedAt);

        if (query.status() != null) {
            wrapper.eq(ArchivePO::getStatus, query.status());
        }
        if (query.currentApplicationId() != null) {
            wrapper.eq(ArchivePO::getCurrentApplicationId, query.currentApplicationId());
        }

        wrapper.orderByDesc(ArchivePO::getCreatedAt);

        IPage<ArchivePO> poPage = archiveMapper.selectPage(page, wrapper);
        return poPage.convert(ArchiveConverter::toDomain);
    }

    @Override
    public boolean softDelete(Long id) {
        ArchivePO po = new ArchivePO();
        po.setId(id);
        po.setDeletedAt(LocalDateTime.now());
        return archiveMapper.updateById(po) > 0;
    }

    @Override
    public void softDeleteBatch(List<Long> ids) {
        ids.forEach(this::softDelete);
    }
}
```

- [ ] **Step 3: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 11: ApplicationRepositoryImpl

**Files:**
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/infrastructure/ApplicationConverter.java`
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/infrastructure/ApplicationRepositoryImpl.java`

- [ ] **Step 1: 创建 ApplicationConverter**

```java
package com.bosyon.zisnackdesk.archive.infrastructure;

import com.bosyon.zisnackdesk.archive.domain.Application;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ApplicationPO;

public class ApplicationConverter {

    public static ApplicationPO toPO(Application domain) {
        if (domain == null) return null;
        ApplicationPO po = new ApplicationPO();
        po.setId(domain.getId());
        po.setApplicantId(domain.getApplicantId());
        po.setStatus(domain.getStatus());
        po.setCreatedAt(domain.getCreatedAt());
        po.setDeletedAt(domain.getDeletedAt());
        po.setCreatedBy(domain.getCreatedBy());
        po.setUpdatedBy(domain.getUpdatedBy());
        po.setUpdatedAt(domain.getUpdatedAt());
        return po;
    }

    public static Application toDomain(ApplicationPO po) {
        if (po == null) return null;
        Application domain = new Application();
        domain.setId(po.getId());
        domain.setApplicantId(po.getApplicantId());
        domain.setStatus(po.getStatus());
        domain.setCreatedAt(po.getCreatedAt());
        domain.setDeletedAt(po.getDeletedAt());
        domain.setCreatedBy(po.getCreatedBy());
        domain.setUpdatedBy(po.getUpdatedBy());
        domain.setUpdatedAt(po.getUpdatedAt());
        return domain;
    }
}
```

- [ ] **Step 2: 创建 ApplicationRepositoryImpl**

```java
package com.bosyon.zisnackdesk.archive.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.Application;
import com.bosyon.zisnackdesk.archive.domain.ApplicationRepository;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ApplicationPO;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationQueryRequest;
import com.bosyon.zisnackdesk.mapper.ApplicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepository {

    private final ApplicationMapper applicationMapper;

    @Override
    public Application save(Application application) {
        ApplicationPO po = ApplicationConverter.toPO(application);
        applicationMapper.insert(po);
        application.setId(po.getId());
        application.setCreatedAt(po.getCreatedAt());
        return application;
    }

    @Override
    public Application update(Application application) {
        ApplicationPO po = ApplicationConverter.toPO(application);
        applicationMapper.updateById(po);
        return application;
    }

    @Override
    public Optional<Application> findById(Long id) {
        return Optional.ofNullable(applicationMapper.selectById(id))
                .map(ApplicationConverter::toDomain);
    }

    @Override
    public IPage<Application> query(ApplicationQueryRequest query, Page<?> page) {
        LambdaQueryWrapper<ApplicationPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(ApplicationPO::getDeletedAt);

        if (query.applicantId() != null) {
            wrapper.eq(ApplicationPO::getApplicantId, query.applicantId());
        }
        if (query.status() != null) {
            wrapper.eq(ApplicationPO::getStatus, query.status());
        }

        wrapper.orderByDesc(ApplicationPO::getCreatedAt);

        IPage<ApplicationPO> poPage = applicationMapper.selectPage(page, wrapper);
        return poPage.convert(ApplicationConverter::toDomain);
    }

    @Override
    public boolean softDelete(Long id) {
        ApplicationPO po = new ApplicationPO();
        po.setId(id);
        po.setDeletedAt(LocalDateTime.now());
        return applicationMapper.updateById(po) > 0;
    }

    @Override
    public void softDeleteBatch(List<Long> ids) {
        ids.forEach(this::softDelete);
    }
}
```

- [ ] **Step 3: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 12: ApplicationDetailRepositoryImpl

**Files:**
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/infrastructure/ApplicationDetailConverter.java`
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/infrastructure/ApplicationDetailRepositoryImpl.java`

- [ ] **Step 1: 创建 ApplicationDetailConverter**

```java
package com.bosyon.zisnackdesk.archive.infrastructure;

import com.bosyon.zisnackdesk.archive.domain.ApplicationDetail;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ApplicationDetailPO;

public class ApplicationDetailConverter {

    public static ApplicationDetailPO toPO(ApplicationDetail domain) {
        if (domain == null) return null;
        ApplicationDetailPO po = new ApplicationDetailPO();
        po.setId(domain.getId());
        po.setApplicationId(domain.getApplicationId());
        po.setArchiveId(domain.getArchiveId());
        po.setDeletedAt(domain.getDeletedAt());
        po.setCreatedBy(domain.getCreatedBy());
        po.setUpdatedBy(domain.getUpdatedBy());
        po.setCreatedAt(domain.getCreatedAt());
        po.setUpdatedAt(domain.getUpdatedAt());
        return po;
    }

    public static ApplicationDetail toDomain(ApplicationDetailPO po) {
        if (po == null) return null;
        ApplicationDetail domain = new ApplicationDetail();
        domain.setId(po.getId());
        domain.setApplicationId(po.getApplicationId());
        domain.setArchiveId(po.getArchiveId());
        domain.setDeletedAt(po.getDeletedAt());
        domain.setCreatedBy(po.getCreatedBy());
        domain.setUpdatedBy(po.getUpdatedBy());
        domain.setCreatedAt(po.getCreatedAt());
        domain.setUpdatedAt(po.getUpdatedAt());
        return domain;
    }
}
```

- [ ] **Step 2: 创建 ApplicationDetailRepositoryImpl**

```java
package com.bosyon.zisnackdesk.archive.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.ApplicationDetail;
import com.bosyon.zisnackdesk.archive.domain.ApplicationDetailRepository;
import com.bosyon.zisnackdesk.archive.infrastructure.po.ApplicationDetailPO;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailQueryRequest;
import com.bosyon.zisnackdesk.mapper.ApplicationDetailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApplicationDetailRepositoryImpl implements ApplicationDetailRepository {

    private final ApplicationDetailMapper applicationDetailMapper;

    @Override
    public ApplicationDetail save(ApplicationDetail detail) {
        ApplicationDetailPO po = ApplicationDetailConverter.toPO(detail);
        applicationDetailMapper.insert(po);
        detail.setId(po.getId());
        detail.setCreatedAt(po.getCreatedAt());
        return detail;
    }

    @Override
    public ApplicationDetail update(ApplicationDetail detail) {
        ApplicationDetailPO po = ApplicationDetailConverter.toPO(detail);
        applicationDetailMapper.updateById(po);
        return detail;
    }

    @Override
    public Optional<ApplicationDetail> findById(Long id) {
        return Optional.ofNullable(applicationDetailMapper.selectById(id))
                .map(ApplicationDetailConverter::toDomain);
    }

    @Override
    public IPage<ApplicationDetail> query(ApplicationDetailQueryRequest query, Page<?> page) {
        LambdaQueryWrapper<ApplicationDetailPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(ApplicationDetailPO::getDeletedAt);

        if (query.applicationId() != null) {
            wrapper.eq(ApplicationDetailPO::getApplicationId, query.applicationId());
        }
        if (query.archiveId() != null) {
            wrapper.eq(ApplicationDetailPO::getArchiveId, query.archiveId());
        }

        wrapper.orderByDesc(ApplicationDetailPO::getCreatedAt);

        IPage<ApplicationDetailPO> poPage = applicationDetailMapper.selectPage(page, wrapper);
        return poPage.convert(ApplicationDetailConverter::toDomain);
    }

    @Override
    public boolean softDelete(Long id) {
        ApplicationDetailPO po = new ApplicationDetailPO();
        po.setId(id);
        po.setDeletedAt(LocalDateTime.now());
        return applicationDetailMapper.updateById(po) > 0;
    }

    @Override
    public void softDeleteBatch(List<Long> ids) {
        ids.forEach(this::softDelete);
    }
}
```

- [ ] **Step 3: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 13: ArchiveApplicationService

**File:** Create: `src/main/java/com/bosyon/zisnackdesk/archive/application/ArchiveApplicationService.java`

- [ ] **Step 1: 创建 ArchiveApplicationService**

```java
package com.bosyon.zisnackdesk.archive.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.Archive;
import com.bosyon.zisnackdesk.archive.domain.ArchiveRepository;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ArchiveApplicationService {

    private final ArchiveRepository archiveRepository;

    public ArchiveResponse createArchive(ArchiveCreateRequest request) {
        Archive archive = new Archive(request.status(), request.currentApplicationId());
        archive.validateForCreate();
        Archive saved = archiveRepository.save(archive);
        log.info("创建档案成功, id: {}", saved.getId());
        return saved.toVO();
    }

    public ArchiveResponse updateArchive(ArchiveUpdateRequest request) {
        Archive archive = archiveRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("档案不存在, id: " + request.id()));
        archive.applyCreate(request.status(), request.currentApplicationId());
        archive.validateForUpdate();
        Archive saved = archiveRepository.update(archive);
        log.info("更新档案成功, id: {}", saved.getId());
        return saved.toVO();
    }

    public ArchiveResponse getArchiveById(Long id) {
        return archiveRepository.findById(id)
                .map(Archive::toVO)
                .orElse(null);
    }

    public IPage<ArchiveResponse> queryArchives(ArchiveQueryRequest request, int pageNum, int pageSize) {
        return archiveRepository.query(request, new Page<>(pageNum, pageSize))
                .convert(Archive::toVO);
    }

    public boolean deleteArchive(Long id) {
        return archiveRepository.findById(id)
                .map(archive -> {
                    archive.markAsDeleted();
                    archiveRepository.update(archive);
                    log.info("软删除档案成功, id: {}", id);
                    return true;
                })
                .orElse(false);
    }

    public boolean batchDeleteArchives(List<Long> ids) {
        archiveRepository.softDeleteBatch(ids);
        log.info("批量软删除档案成功, ids: {}", ids);
        return true;
    }
}
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 14: ApplicationApplicationService

**File:** Create: `src/main/java/com/bosyon/zisnackdesk/archive/application/ApplicationApplicationService.java`

- [ ] **Step 1: 创建 ApplicationApplicationService**

```java
package com.bosyon.zisnackdesk.archive.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.Application;
import com.bosyon.zisnackdesk.archive.domain.ApplicationRepository;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationResponse createApplication(ApplicationCreateRequest request) {
        Application app = new Application(request.applicantId(), request.status());
        app.validateForCreate();
        Application saved = applicationRepository.save(app);
        log.info("创建申请单成功, id: {}", saved.getId());
        return saved.toVO();
    }

    public ApplicationResponse updateApplication(ApplicationUpdateRequest request) {
        Application app = applicationRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("申请单不存在, id: " + request.id()));
        app.applyUpdate(request.applicantId(), request.status());
        app.validateForUpdate();
        Application saved = applicationRepository.update(app);
        log.info("更新申请单成功, id: {}", saved.getId());
        return saved.toVO();
    }

    public ApplicationResponse getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .map(Application::toVO)
                .orElse(null);
    }

    public IPage<ApplicationResponse> queryApplications(ApplicationQueryRequest request, int pageNum, int pageSize) {
        return applicationRepository.query(request, new Page<>(pageNum, pageSize))
                .convert(Application::toVO);
    }

    public boolean deleteApplication(Long id) {
        return applicationRepository.findById(id)
                .map(app -> {
                    app.markAsDeleted();
                    applicationRepository.update(app);
                    log.info("软删除申请单成功, id: {}", id);
                    return true;
                })
                .orElse(false);
    }

    public boolean batchDeleteApplications(List<Long> ids) {
        applicationRepository.softDeleteBatch(ids);
        log.info("批量软删除申请单成功, ids: {}", ids);
        return true;
    }
}
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 15: ApplicationDetailApplicationService

**File:** Create: `src/main/java/com/bosyon/zisnackdesk/archive/application/ApplicationDetailApplicationService.java`

- [ ] **Step 1: 创建 ApplicationDetailApplicationService**

```java
package com.bosyon.zisnackdesk.archive.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.ApplicationDetail;
import com.bosyon.zisnackdesk.archive.domain.ApplicationDetailRepository;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationDetailApplicationService {

    private final ApplicationDetailRepository applicationDetailRepository;

    public ApplicationDetailResponse createDetail(ApplicationDetailCreateRequest request) {
        ApplicationDetail detail = new ApplicationDetail(request.applicationId(), request.archiveId());
        detail.validateForCreate();
        ApplicationDetail saved = applicationDetailRepository.save(detail);
        log.info("创建申请单明细成功, id: {}", saved.getId());
        return saved.toVO();
    }

    public ApplicationDetailResponse updateDetail(ApplicationDetailUpdateRequest request) {
        ApplicationDetail detail = applicationDetailRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("申请单明细不存在, id: " + request.id()));
        detail.applyCreate(request.applicationId(), request.archiveId());
        detail.validateForUpdate();
        ApplicationDetail saved = applicationDetailRepository.update(detail);
        log.info("更新申请单明细成功, id: {}", saved.getId());
        return saved.toVO();
    }

    public ApplicationDetailResponse getDetailById(Long id) {
        return applicationDetailRepository.findById(id)
                .map(ApplicationDetail::toVO)
                .orElse(null);
    }

    public IPage<ApplicationDetailResponse> queryDetails(ApplicationDetailQueryRequest request, int pageNum, int pageSize) {
        return applicationDetailRepository.query(request, new Page<>(pageNum, pageSize))
                .convert(ApplicationDetail::toVO);
    }

    public boolean deleteDetail(Long id) {
        return applicationDetailRepository.findById(id)
                .map(detail -> {
                    detail.markAsDeleted();
                    applicationDetailRepository.update(detail);
                    log.info("软删除申请单明细成功, id: {}", id);
                    return true;
                })
                .orElse(false);
    }

    public boolean batchDeleteDetails(List<Long> ids) {
        applicationDetailRepository.softDeleteBatch(ids);
        log.info("批量软删除申请单明细成功, ids: {}", ids);
        return true;
    }
}
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 16: Archive DTO/VO

**Files:**
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/dto/ArchiveCreateRequest.java`
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/dto/ArchiveUpdateRequest.java`
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/dto/ArchiveQueryRequest.java`
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/dto/ArchiveResponse.java`

- [ ] **Step 1: 创建 ArchiveCreateRequest**

```java
package com.bosyon.zisnackdesk.archive.interfaces.dto;

public record ArchiveCreateRequest(
        Integer status,
        Long currentApplicationId
) {}
```

- [ ] **Step 2: 创建 ArchiveUpdateRequest**

```java
package com.bosyon.zisnackdesk.archive.interfaces.dto;

import jakarta.validation.constraints.NotNull;

public record ArchiveUpdateRequest(
        @NotNull(message = "id 不能为空")
        Long id,
        Integer status,
        Long currentApplicationId
) {}
```

- [ ] **Step 3: 创建 ArchiveQueryRequest**

```java
package com.bosyon.zisnackdesk.archive.interfaces.dto;

public record ArchiveQueryRequest(
        Integer status,
        Long currentApplicationId
) {}
```

- [ ] **Step 4: 创建 ArchiveResponse**

```java
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
```

---

### Task 17: Application DTO/VO

**Files:**
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/dto/ApplicationCreateRequest.java`
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/dto/ApplicationUpdateRequest.java`
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/dto/ApplicationQueryRequest.java`
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/dto/ApplicationResponse.java`

- [ ] **Step 1: 创建 ApplicationCreateRequest**

```java
package com.bosyon.zisnackdesk.archive.interfaces.dto;

public record ApplicationCreateRequest(
        Long applicantId,
        Integer status
) {}
```

- [ ] **Step 2: 创建 ApplicationUpdateRequest**

```java
package com.bosyon.zisnackdesk.archive.interfaces.dto;

import jakarta.validation.constraints.NotNull;

public record ApplicationUpdateRequest(
        @NotNull(message = "id 不能为空")
        Long id,
        Long applicantId,
        Integer status
) {}
```

- [ ] **Step 3: 创建 ApplicationQueryRequest**

```java
package com.bosyon.zisnackdesk.archive.interfaces.dto;

public record ApplicationQueryRequest(
        Long applicantId,
        Integer status
) {}
```

- [ ] **Step 4: 创建 ApplicationResponse**

```java
package com.bosyon.zisnackdesk.archive.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationResponse {
    private Long id;
    private Long applicantId;
    private Integer status;
    private LocalDateTime createdAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
```

---

### Task 18: ApplicationDetail DTO/VO

**Files:**
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/dto/ApplicationDetailCreateRequest.java`
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/dto/ApplicationDetailUpdateRequest.java`
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/dto/ApplicationDetailQueryRequest.java`
- Create: `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/dto/ApplicationDetailResponse.java`

- [ ] **Step 1: 创建 ApplicationDetailCreateRequest**

```java
package com.bosyon.zisnackdesk.archive.interfaces.dto;

public record ApplicationDetailCreateRequest(
        Long applicationId,
        Long archiveId
) {}
```

- [ ] **Step 2: 创建 ApplicationDetailUpdateRequest**

```java
package com.bosyon.zisnackdesk.archive.interfaces.dto;

import jakarta.validation.constraints.NotNull;

public record ApplicationDetailUpdateRequest(
        @NotNull(message = "id 不能为空")
        Long id,
        Long applicationId,
        Long archiveId
) {}
```

- [ ] **Step 3: 创建 ApplicationDetailQueryRequest**

```java
package com.bosyon.zisnackdesk.archive.interfaces.dto;

public record ApplicationDetailQueryRequest(
        Long applicationId,
        Long archiveId
) {}
```

- [ ] **Step 4: 创建 ApplicationDetailResponse**

```java
package com.bosyon.zisnackdesk.archive.interfaces.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationDetailResponse {
    private Long id;
    private Long applicationId;
    private Long archiveId;
    private LocalDateTime createdAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
```

---

### Task 19: 迁移 ArchiveController

**File:** Move+Modify: `src/main/java/com/bosyon/zisnackdesk/controller/ArchiveController.java` → `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/ArchiveController.java`

- [ ] **Step 1: 创建新的 ArchiveController（指向新 ApplicationService + 新 DTO）**

```java
package com.bosyon.zisnackdesk.archive.interfaces;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bosyon.zisnackdesk.archive.application.ArchiveApplicationService;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/archive")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ArchiveController {

    private final ArchiveApplicationService archiveService;

    @PostMapping
    public ArchiveResponse createArchive(@Valid @RequestBody ArchiveCreateRequest request) {
        return archiveService.createArchive(request);
    }

    @PutMapping
    public ArchiveResponse updateArchive(@Valid @RequestBody ArchiveUpdateRequest request) {
        return archiveService.updateArchive(request);
    }

    @GetMapping("/{id}")
    public ArchiveResponse getArchiveById(@PathVariable @NotNull Long id) {
        return archiveService.getArchiveById(id);
    }

    @GetMapping("/list")
    public IPage<ArchiveResponse> queryArchives(ArchiveQueryRequest request,
                                                @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                                @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        return archiveService.queryArchives(request, pageNum, pageSize);
    }

    @DeleteMapping("/{id}")
    public boolean deleteArchive(@PathVariable @NotNull Long id) {
        return archiveService.deleteArchive(id);
    }

    @PostMapping("/batch-delete")
    public boolean batchDeleteArchives(@RequestBody @NotEmpty List<Long> ids) {
        return archiveService.batchDeleteArchives(ids);
    }
}
```

- [ ] **Step 2: 删除旧 ArchiveController**

Delete: `src/main/java/com/bosyon/zisnackdesk/controller/ArchiveController.java`

---

### Task 20: 迁移 ApplicationController

**File:** Move+Modify: `src/main/java/com/bosyon/zisnackdesk/controller/ApplicationController.java` → `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/ApplicationController.java`

- [ ] **Step 1: 创建新的 ApplicationController**

```java
package com.bosyon.zisnackdesk.archive.interfaces;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bosyon.zisnackdesk.archive.application.ApplicationApplicationService;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ApplicationController {

    private final ApplicationApplicationService applicationService;

    @PostMapping
    public ApplicationResponse createApplication(@Valid @RequestBody ApplicationCreateRequest request) {
        return applicationService.createApplication(request);
    }

    @PutMapping
    public ApplicationResponse updateApplication(@Valid @RequestBody ApplicationUpdateRequest request) {
        return applicationService.updateApplication(request);
    }

    @GetMapping("/{id}")
    public ApplicationResponse getApplicationById(@PathVariable @NotNull Long id) {
        return applicationService.getApplicationById(id);
    }

    @GetMapping("/list")
    public IPage<ApplicationResponse> queryApplications(ApplicationQueryRequest request,
                                                        @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        return applicationService.queryApplications(request, pageNum, pageSize);
    }

    @DeleteMapping("/{id}")
    public boolean deleteApplication(@PathVariable @NotNull Long id) {
        return applicationService.deleteApplication(id);
    }

    @PostMapping("/batch-delete")
    public boolean batchDeleteApplications(@RequestBody @NotEmpty List<Long> ids) {
        return applicationService.batchDeleteApplications(ids);
    }
}
```

- [ ] **Step 2: 删除旧 ApplicationController**

Delete: `src/main/java/com/bosyon/zisnackdesk/controller/ApplicationController.java`

---

### Task 21: 迁移 ApplicationDetailController

**File:** Move+Modify: `src/main/java/com/bosyon/zisnackdesk/controller/ApplicationDetailController.java` → `src/main/java/com/bosyon/zisnackdesk/archive/interfaces/ApplicationDetailController.java`

- [ ] **Step 1: 创建新的 ApplicationDetailController**

```java
package com.bosyon.zisnackdesk.archive.interfaces;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bosyon.zisnackdesk.archive.application.ApplicationDetailApplicationService;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailUpdateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application-detail")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ApplicationDetailController {

    private final ApplicationDetailApplicationService applicationDetailService;

    @PostMapping
    public ApplicationDetailResponse createDetail(@Valid @RequestBody ApplicationDetailCreateRequest request) {
        return applicationDetailService.createDetail(request);
    }

    @PutMapping
    public ApplicationDetailResponse updateDetail(@Valid @RequestBody ApplicationDetailUpdateRequest request) {
        return applicationDetailService.updateDetail(request);
    }

    @GetMapping("/{id}")
    public ApplicationDetailResponse getDetailById(@PathVariable @NotNull Long id) {
        return applicationDetailService.getDetailById(id);
    }

    @GetMapping("/list")
    public IPage<ApplicationDetailResponse> queryDetails(ApplicationDetailQueryRequest request,
                                                         @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        return applicationDetailService.queryDetails(request, pageNum, pageSize);
    }

    @DeleteMapping("/{id}")
    public boolean deleteDetail(@PathVariable @NotNull Long id) {
        return applicationDetailService.deleteDetail(id);
    }

    @PostMapping("/batch-delete")
    public boolean batchDeleteDetails(@RequestBody @NotEmpty List<Long> ids) {
        return applicationDetailService.batchDeleteDetails(ids);
    }
}
```

- [ ] **Step 2: 删除旧 ApplicationDetailController**

Delete: `src/main/java/com/bosyon/zisnackdesk/controller/ApplicationDetailController.java`

---

### Task 22: 删除旧 Model 类

**Files to delete:**
- `src/main/java/com/bosyon/zisnackdesk/model/Archive.java`
- `src/main/java/com/bosyon/zisnackdesk/model/Application.java`
- `src/main/java/com/bosyon/zisnackdesk/model/ApplicationDetail.java`
- `src/main/java/com/bosyon/zisnackdesk/model/dto/ArchiveCreateDTO.java`
- `src/main/java/com/bosyon/zisnackdesk/model/dto/ArchiveUpdateDTO.java`
- `src/main/java/com/bosyon/zisnackdesk/model/dto/ArchiveQueryDTO.java`
- `src/main/java/com/bosyon/zisnackdesk/model/dto/ApplicationCreateDTO.java`
- `src/main/java/com/bosyon/zisnackdesk/model/dto/ApplicationUpdateDTO.java`
- `src/main/java/com/bosyon/zisnackdesk/model/dto/ApplicationQueryDTO.java`
- `src/main/java/com/bosyon/zisnackdesk/model/dto/ApplicationDetailCreateDTO.java`
- `src/main/java/com/bosyon/zisnackdesk/model/dto/ApplicationDetailUpdateDTO.java`
- `src/main/java/com/bosyon/zisnackdesk/model/dto/ApplicationDetailQueryDTO.java`
- `src/main/java/com/bosyon/zisnackdesk/model/vo/ArchiveVO.java`
- `src/main/java/com/bosyon/zisnackdesk/model/vo/ApplicationVO.java`
- `src/main/java/com/bosyon/zisnackdesk/model/vo/ApplicationDetailVO.java`

- [ ] **Step 1: 删除所有旧 Model 文件**

确认所有 Controller 和 Service 已迁移后，逐个删除上述文件。

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

---

### Task 23: 删除旧 Service 类

**Files to delete:**
- `src/main/java/com/bosyon/zisnackdesk/service/ArchiveService.java`
- `src/main/java/com/bosyon/zisnackdesk/service/ApplicationService.java`
- `src/main/java/com/bosyon/zisnackdesk/service/ApplicationDetailService.java`
- `src/main/java/com/bosyon/zisnackdesk/service/impl/ArchiveServiceImpl.java`
- `src/main/java/com/bosyon/zisnackdesk/service/impl/ApplicationServiceImpl.java`
- `src/main/java/com/bosyon/zisnackdesk/service/impl/ApplicationDetailServiceImpl.java`

- [ ] **Step 1: 删除所有旧 Service 文件**

确认 ApplicationService 已全部替换后，删除上述文件。

- [ ] **Step 2: 验证编译 + 运行测试**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

Run: `mvn test`
Expected: All tests pass

---

### Task 24: 最终验证

- [ ] **Step 1: 全量编译**

Run: `mvn -DskipTests package -q`
Expected: BUILD SUCCESS

- [ ] **Step 2: 运行测试**

Run: `mvn test`
Expected: All tests pass, 0 failures

- [ ] **Step 3: 提交**

```bash
git add -A
git commit -m "refactor(archive): 引入轻量级DDD半血模型改造档案界限上下文

- Archive/Application/ApplicationDetail 拆分为 PO + Domain 双模型
- Domain 实体加入自校验(validateForCreate/Update)和行为(markAsDeleted/submit/approve/reject)
- 新增 Repository 接口与实现，封装 MyBatis-Plus 调用
- ApplicationService 变薄，仅做编排
- 按界限上下文分包（archive/），DTO/VO 归入 interfaces/dto/
- 删除旧贫血模型和 Service 类
"
```
