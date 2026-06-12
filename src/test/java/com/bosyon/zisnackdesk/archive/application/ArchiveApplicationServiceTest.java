package com.bosyon.zisnackdesk.archive.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.Archive;
import com.bosyon.zisnackdesk.archive.domain.ArchiveRepository;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ArchiveUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArchiveApplicationService 单元测试")
class ArchiveApplicationServiceTest {

    @Mock
    private ArchiveRepository archiveRepository;

    @InjectMocks
    private ArchiveApplicationService archiveService;

    @Captor
    private ArgumentCaptor<Archive> archiveCaptor;

    @Nested
    @DisplayName("创建档案")
    class CreateArchiveTests {

        @Test
        @DisplayName("创建档案成功，返回 ArchiveResponse")
        void createArchive_success() {
            // given
            ArchiveCreateRequest request = new ArchiveCreateRequest(1, 100L);

            when(archiveRepository.save(any(Archive.class))).thenAnswer(invocation -> {
                Archive a = invocation.getArgument(0);
                a.setId(1L);
                a.setCreatedAt(LocalDateTime.now());
                return a;
            });

            // when
            ArchiveResponse response = archiveService.createArchive(request);

            // then
            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals(1, response.getStatus());
            assertEquals(100L, response.getCurrentApplicationId());

            verify(archiveRepository).save(archiveCaptor.capture());
            Archive saved = archiveCaptor.getValue();
            assertEquals(1, saved.getStatus());
            assertEquals(100L, saved.getCurrentApplicationId());
        }

        @Test
        @DisplayName("状态值不合法时抛出异常")
        void createArchive_invalidStatus_throws() {
            ArchiveCreateRequest request = new ArchiveCreateRequest(-1, 100L);

            assertThrows(IllegalArgumentException.class, () -> archiveService.createArchive(request));
            verify(archiveRepository, never()).save(any());
        }

        @Test
        @DisplayName("关联申请单为空时抛出异常")
        void createArchive_nullApplication_throws() {
            ArchiveCreateRequest request = new ArchiveCreateRequest(1, null);

            assertThrows(IllegalArgumentException.class, () -> archiveService.createArchive(request));
            verify(archiveRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("更新档案")
    class UpdateArchiveTests {

        @Test
        @DisplayName("更新存在的档案并返回 ArchiveResponse")
        void updateArchive_success() {
            // given
            Archive existing = new Archive(0, 100L);
            existing.setId(1L);

            when(archiveRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(archiveRepository.update(any(Archive.class))).thenAnswer(inv -> inv.getArgument(0));

            ArchiveUpdateRequest request = new ArchiveUpdateRequest(1L, 2, 200L);

            // when
            ArchiveResponse response = archiveService.updateArchive(request);

            // then
            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals(2, response.getStatus());
            assertEquals(200L, response.getCurrentApplicationId());

            verify(archiveRepository).update(archiveCaptor.capture());
            Archive updated = archiveCaptor.getValue();
            assertEquals(2, updated.getStatus());
            assertEquals(200L, updated.getCurrentApplicationId());
        }

        @Test
        @DisplayName("档案不存在时抛出异常")
        void updateArchive_notFound_throws() {
            when(archiveRepository.findById(99L)).thenReturn(Optional.empty());

            ArchiveUpdateRequest request = new ArchiveUpdateRequest(99L, 1, 100L);

            assertThrows(RuntimeException.class, () -> archiveService.updateArchive(request));
            verify(archiveRepository, never()).update(any());
        }
    }

    @Nested
    @DisplayName("查询/分页")
    class QueryTests {

        @Test
        @DisplayName("分页查询返回 ArchiveResponse 列表")
        void queryArchives_returnsConvertedPage() {
            // given
            Page<Archive> page = new Page<>(1, 10);
            Archive archive = new Archive(1, 100L);
            archive.setId(10L);
            page.setRecords(List.of(archive));

            when(archiveRepository.query(any(ArchiveQueryRequest.class), eq(1), eq(10))).thenReturn(page);

            ArchiveQueryRequest query = new ArchiveQueryRequest(null, null);

            // when
            IPage<ArchiveResponse> res = archiveService.queryArchives(query, 1, 10);

            // then
            assertNotNull(res);
            assertEquals(1, res.getRecords().size());
            assertEquals(10L, res.getRecords().get(0).getId());
        }

        @Test
        @DisplayName("根据 ID 查询返回 ArchiveResponse")
        void getArchiveById_returnsResponse() {
            Archive archive = new Archive(1, 100L);
            archive.setId(5L);
            when(archiveRepository.findById(5L)).thenReturn(Optional.of(archive));

            ArchiveResponse response = archiveService.getArchiveById(5L);

            assertNotNull(response);
            assertEquals(5L, response.getId());
            assertEquals(1, response.getStatus());
        }

        @Test
        @DisplayName("根据 ID 查询不存在时返回 null")
        void getArchiveById_notFound_returnsNull() {
            when(archiveRepository.findById(99L)).thenReturn(Optional.empty());
            assertNull(archiveService.getArchiveById(99L));
        }
    }

    @Nested
    @DisplayName("删除")
    class DeleteTests {

        @Test
        @DisplayName("档案不存在时返回 false")
        void deleteArchive_notFound_returnsFalse() {
            when(archiveRepository.findById(99L)).thenReturn(Optional.empty());
            assertFalse(archiveService.deleteArchive(99L));
        }

        @Test
        @DisplayName("删除存在的档案，设置 deletedAt 并返回 true")
        void deleteArchive_success() {
            Archive archive = new Archive(1, 100L);
            archive.setId(10L);
            when(archiveRepository.findById(10L)).thenReturn(Optional.of(archive));
            when(archiveRepository.update(any(Archive.class))).thenAnswer(inv -> inv.getArgument(0));

            boolean result = archiveService.deleteArchive(10L);
            assertTrue(result);

            verify(archiveRepository).update(archiveCaptor.capture());
            Archive updated = archiveCaptor.getValue();
            assertNotNull(updated.getDeletedAt());
        }

        @Test
        @DisplayName("批量删除直接调用 repository.softDeleteBatch")
        void batchDeleteArchives_callsSoftDeleteBatch() {
            List<Long> ids = List.of(1L, 2L);
            doNothing().when(archiveRepository).softDeleteBatch(ids);

            boolean result = archiveService.batchDeleteArchives(ids);
            assertTrue(result);

            verify(archiveRepository).softDeleteBatch(ids);
        }
    }
}
