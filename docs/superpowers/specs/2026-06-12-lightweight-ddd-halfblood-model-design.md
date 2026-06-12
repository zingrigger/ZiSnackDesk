# 轻量级 DDD · 半血模型改造设计

> ZiSnackDesk — 从贫血模型到半血模型的渐进式架构改造
> 日期：2026-06-12

## 1. 背景与目标

当前项目采用典型的**贫血模型**架构：Model 层使用 Lombok `@Data` 作为纯数据容器，所有业务逻辑（校验、计算、状态变更、DTO/VO 转换）集中在 Service 层，导致 Service 层臃肿且业务规则难以复用。

**改造目标**：引入**轻量级 DDD + 半血模型**，让实体拥有自校验、简单计算、状态行为等能力，同时保持架构简洁，不引入额外框架。

### 核心原则

- **轻量**：不引入完整 DDD 战术模式（无 Domain Event、Specification、Value Object 等）
- **渐进**：按业务模块独立推进，可随时回退
- **半血**：实体只做"不依赖外部查询"的逻辑；需查库的规则保留在 ApplicationService
- **兼容**：保留 MyBatis-Plus `BaseMapper<T>` 现有用法

## 2. 包结构

```
com.bosyon.zisnackdesk
│
├── common
│   └── dto
│       ├── PageRequest.java
│       └── PageResponse.java
│
├── archive                                # 📁 档案申请界限上下文
│   ├── interfaces
│   │   ├── ArchiveController.java
│   │   ├── ApplicationController.java
│   │   └── ApplicationDetailController.java
│   ├── application
│   │   ├── ArchiveApplicationService.java
│   │   ├── ApplicationApplicationService.java
│   │   └── ApplicationDetailApplicationService.java
│   ├── domain
│   │   ├── Archive.java                  # 半血实体
│   │   ├── Application.java              # 半血实体
│   │   ├── ApplicationStatus.java        # 状态枚举
│   │   ├── ApplicationDetail.java        # 半血实体
│   │   ├── ArchiveRepository.java
│   │   ├── ApplicationRepository.java
│   │   └── ApplicationDetailRepository.java
│   └── infrastructure
│       ├── ArchiveRepositoryImpl.java
│       ├── ApplicationRepositoryImpl.java
│       ├── ApplicationDetailRepositoryImpl.java
│       └── po
│           ├── ArchivePO.java
│           ├── ApplicationPO.java
│           └── ApplicationDetailPO.java
│
├── user                                   # 📁 用户界限上下文
│   ├── interfaces
│   │   └── SysUserController.java
│   ├── application
│   │   └── SysUserApplicationService.java
│   ├── domain
│   │   ├── SysUser.java
│   │   └── SysUserRepository.java
│   └── infrastructure
│       ├── SysUserRepositoryImpl.java
│       └── po
│           └── SysUserPO.java
│
└── config
    ├── MyBatisPlusConfig.java
    ├── RedisConfig.java
    └── SpringContextHolder.java
```

### 各层职责

| 层 | 职责 | 依赖 |
|----|------|------|
| `interfaces/` | REST 接收请求、参数校验、返回响应 | 依赖 `application/` 层 |
| `application/` | 编排仓储调用、事务管理、DTO↔Domain 转换 | 依赖 `domain/` 层接口 |
| `domain/` | 半血实体（自校验 + 行为）+ 仓储接口定义 | 无外部依赖 |
| `infrastructure/` | 仓储实现（MyBatis-Plus）+ PO 数据库映射 | 依赖 `domain/` 层接口 |

## 3. 详细设计

### 3.1 Domain 层 — 半血实体

每个实体包含三类方法：

1. **自校验方法**（`validateForCreate()` / `validateForUpdate()`）— 字段格式、必填、状态转换合法性
2. **状态行为方法**（`markAsDeleted()` / `submit()` / `approve()` / `reject()`）— 实体内部状态变更
3. **转换方法**（`toVO()`）— 统一的 Domain→VO 映射

#### Archive.java

```java
public class Archive {
    private Long id;
    private Integer status;
    private Long currentApplicationId;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public void validateForCreate() {
        if (status == null || status < 0)
            throw new IllegalArgumentException("状态值不合法");
        if (currentApplicationId == null)
            throw new IllegalArgumentException("关联申请单不能为空");
    }

    public void validateForUpdate() {
        if (id == null)
            throw new IllegalArgumentException("ID 不能为空");
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public ArchiveVO toVO() { /* 手动映射 */ }
}
```

#### Application.java

```java
public class Application {
    private Long id;
    private Long applicantId;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public void validateForCreate() { /* 校验 applicantId、status */ }

    public void submit()     { /* 状态 → 已提交，校验当前状态 */ }
    public void approve()    { /* 状态 → 已通过，校验当前状态 */ }
    public void reject()     { /* 状态 → 已驳回，校验当前状态 */ }

    public void markAsDeleted() { this.deletedAt = LocalDateTime.now(); }
    public ApplicationVO toVO() { /* 手动映射 */ }
}
```

#### ApplicationDetail.java

```java
public class ApplicationDetail {
    private Long id;
    private Long applicationId;
    private Long archiveId;
    private LocalDateTime deletedAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void validateForCreate() {
        if (applicationId == null)
            throw new IllegalArgumentException("申请单 ID 不能为空");
        if (archiveId == null)
            throw new IllegalArgumentException("档案 ID 不能为空");
    }

    public void markAsDeleted() { this.deletedAt = LocalDateTime.now(); }
    public ApplicationDetailVO toVO() { /* 手动映射 */ }
}
```

#### SysUser.java

```java
public class SysUser {
    private String id;
    private String account;
    private String mobile;
    private String email;
    private String password;
    private String userType;
    private Boolean mobileVerified;
    private Boolean emailVerified;
    private LocalDateTime deletedAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void validateForCreate() {
        if (account == null || account.isBlank())
            throw new IllegalArgumentException("账号不能为空");
        if (mobile != null && !mobile.matches("^1[3-9]\\d{9}$"))
            throw new IllegalArgumentException("手机号格式不合法");
        if (email != null && !email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$"))
            throw new IllegalArgumentException("邮箱格式不合法");
    }

    public void verifyMobile()  { this.mobileVerified = true; }
    public void verifyEmail()   { this.emailVerified = true; }

    public void markAsDeleted() { this.deletedAt = LocalDateTime.now(); }
    public SysUserVO toVO() { /* 手动映射，排除 password */ }
}
```

### 3.2 Domain 层 — 仓储接口

仓储接口在 domain 层定义（依赖倒置），infrastructure 层实现。

```java
// ArchiveRepository.java
public interface ArchiveRepository {
    Archive save(Archive archive);
    Archive update(Archive archive);
    Optional<Archive> findById(Long id);
    IPage<Archive> query(ArchiveQueryDTO query, Page<?> page);
    boolean softDelete(Long id);
    void softDeleteBatch(List<Long> ids);
}
```

### 3.3 Infrastructure 层 — PO + 仓储实现

PO（Persistence Object）与数据库表一一对应，使用 MyBatis-Plus 注解。RepositoryImpl 负责 PO ↔ Domain 转换。

```java
// ArchivePO.java
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

// ArchiveRepositoryImpl.java
@Repository
@RequiredArgsConstructor
public class ArchiveRepositoryImpl implements ArchiveRepository {
    private final ArchiveMapper archiveMapper;

    @Override
    public Archive save(Archive archive) {
        ArchivePO po = ArchiveConverter.toPO(archive);
        archiveMapper.insert(po);
        return ArchiveConverter.toDomain(po);
    }

    @Override
    public Archive update(Archive archive) {
        ArchivePO po = ArchiveConverter.toPO(archive);
        archiveMapper.updateById(po);
        return ArchiveConverter.toDomain(po);
    }

    @Override
    public Optional<Archive> findById(Long id) {
        return Optional.ofNullable(archiveMapper.selectById(id))
            .map(ArchiveConverter::toDomain);
    }

    @Override
    public IPage<Archive> query(ArchiveQueryDTO query, Page<?> page) {
        LambdaQueryWrapper<ArchivePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(ArchivePO::getDeletedAt);
        if (query.status() != null) wrapper.eq(ArchivePO::getStatus, query.status());
        if (query.currentApplicationId() != null)
            wrapper.eq(ArchivePO::getCurrentApplicationId, query.currentApplicationId());
        wrapper.orderByDesc(ArchivePO::getCreatedAt);
        IPage<ArchivePO> poPage = archiveMapper.selectPage(page, wrapper);
        return poPage.convert(ArchiveConverter::toDomain);
    }

    @Override
    public boolean softDelete(Long id) {
        return archiveMapper.updateById(
            new ArchivePO().setId(id).setDeletedAt(LocalDateTime.now())
        ) > 0;
    }

    @Override
    public void softDeleteBatch(List<Long> ids) {
        ids.forEach(this::softDelete);
    }
}
```

### 3.4 Application 层 — 薄服务

ApplicationService 只做三件事：
1. 调用仓储获取/持久化数据
2. 调用实体的校验和行为方法
3. 管理事务

```java
@Service
@Transactional
@RequiredArgsConstructor
public class ArchiveApplicationService {
    private final ArchiveRepository archiveRepository;

    public ArchiveVO createArchive(ArchiveCreateDTO dto) {
        Archive archive = new Archive();
        archive.setStatus(dto.status());
        archive.setCurrentApplicationId(dto.currentApplicationId());
        archive.validateForCreate();              // 实体自校验
        Archive saved = archiveRepository.save(archive);  // 仓储持久化
        return saved.toVO();
    }

    public ArchiveVO updateArchive(ArchiveUpdateDTO dto) {
        Archive archive = archiveRepository.findById(dto.id())
            .orElseThrow(() -> new RuntimeException("档案不存在"));
        archive.setStatus(dto.status());
        archive.setCurrentApplicationId(dto.currentApplicationId());
        archive.validateForUpdate();              // 实体自校验
        Archive saved = archiveRepository.update(archive);
        return saved.toVO();
    }

    public ArchiveVO getArchiveVOById(Long id) {
        return archiveRepository.findById(id)
            .map(Archive::toVO)
            .orElse(null);
    }

    public IPage<ArchiveVO> queryArchives(ArchiveQueryDTO dto, int pageNum, int pageSize) {
        return archiveRepository.query(dto, new Page<>(pageNum, pageSize))
            .convert(Archive::toVO);
    }

    public boolean deleteArchive(Long id) {
        return archiveRepository.findById(id)
            .map(archive -> {
                archive.markAsDeleted();                // 实体行为
                archiveRepository.update(archive);
                return true;
            })
            .orElse(false);
    }

    public boolean batchDeleteArchives(List<Long> ids) {
        archiveRepository.softDeleteBatch(ids);
        return true;
    }
}
```

### 3.5 DTO/VO 处理

- **DTO**（如 `ArchiveCreateDTO`）：保持不变，仍使用 Java Record 作为请求参数
- **VO**（如 `ArchiveVO`）：保持不变，仍使用 `@Data` 作为响应对象
- **PO**（如 `ArchivePO`）：新增，与数据库字段一一对应，承载 MyBatis-Plus 注解
- **转换由实体自身完成**：`toVO()` 在 Domain 实体中实现，避免 Service 层的 BeanUtils 散落

### 3.6 校验策略

| 校验类型 | 位置 | 示例 |
|---------|------|------|
| 字段格式/必填 | Domain 实体 `validateForCreate()` | 手机号格式、状态值范围 |
| 状态转换合法性 | Domain 实体状态方法 | 已驳回不能再次驳回 |
| 唯一性/冲突校验 | ApplicationService | 账号唯一（需查库） |
| 跨实体约束 | ApplicationService | 引用的申请单是否存在 |
| HTTP 参数校验 | Controller `@Valid` | 必填参数、长度限制 |

## 4. 改造顺序

按模块独立推进，每个模块步骤相同：

1. **创建 PO 类** — 将现有 Model 的 MyBatis-Plus 注解迁移到 PO
2. **创建 Mapper（BaseMapper<PO>）** — 原 Mapper 指向 PO
3. **创建 Domain 实体** — 纯 POJO，移入行为方法
4. **创建 Repository 接口** — 定义 domain 层仓储方法
5. **实现 RepositoryImpl** — PO↔Domain 转换 + MyBatis-Plus 调用
6. **改造 ApplicationService** — 调用 Repository 替换原有 ServiceImpl
7. **移动 Controller** 到 `interfaces/` 包
8. **删除旧 Service 和 Model** — 确认无引用后清理

**推荐顺序**：Archive → ApplicationDetail → Application → SysUser（从简单到复杂）

## 5. 边界与约束

### 不做的事

- ❌ 不引入完整 DDD 战术模式（Domain Event、Specification、Value Object 等）
- ❌ 不改动 Controller 的 API 签名（保持前端兼容）
- ❌ 不改动数据库表结构
- ❌ 不改动 build 配置和依赖

### 保持的事

- ✅ Controller 使用 `@Valid` + DTO Record 接收请求
- ✅ MyBatis-Plus `BaseMapper<T>` 作为 ORM 基础
- ✅ 分页使用 `IPage` + `Page`
- ✅ 软删除逻辑（`deletedAt` 字段）
- ✅ Logback 日志
- ✅ Lombok 在 PO 和 VO 中使用

## 6. 风险与应对

| 风险 | 影响 | 应对 |
|------|------|------|
| PO 与 Domain 字段同步成本 | 改造期效率降低 | 使用 Converter 统一转换，必要时加自动化测试 |
| 现有代码无单元测试覆盖 | 改造可能引入回归 | 按模块推进，每个模块改造后手动验证核心 CRUD |
| ApplicationService 职责边界模糊 | 逻辑"漏"到 Controller | 严格 Code Review，不允许 Controller 直接调用 Repository |
