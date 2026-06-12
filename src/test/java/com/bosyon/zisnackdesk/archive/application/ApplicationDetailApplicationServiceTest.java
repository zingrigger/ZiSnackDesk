package com.bosyon.zisnackdesk.archive.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.ApplicationDetail;
import com.bosyon.zisnackdesk.archive.domain.ApplicationDetailRepository;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationDetailUpdateRequest;
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
@DisplayName("ApplicationDetailApplicationService 单元测试")
class ApplicationDetailApplicationServiceTest {

    @Mock
    private ApplicationDetailRepository applicationDetailRepository;

    @InjectMocks
    private ApplicationDetailApplicationService detailService;

    @Captor
    private ArgumentCaptor<ApplicationDetail> detailCaptor;

    @Nested
    @DisplayName("创建申请单明细")
    class CreateDetailTests {

        @Test
        @DisplayName("创建明细成功，返回 ApplicationDetailResponse")
        void createDetail_success() {
            // given
            ApplicationDetailCreateRequest request = new ApplicationDetailCreateRequest(1L, 100L);

            when(applicationDetailRepository.save(any(ApplicationDetail.class))).thenAnswer(invocation -> {
                ApplicationDetail d = invocation.getArgument(0);
                d.setId(1L);
                d.setCreatedAt(LocalDateTime.now());
                return d;
            });

            // when
            ApplicationDetailResponse response = detailService.createDetail(request);

            // then
            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals(1L, response.getApplicationId());
            assertEquals(100L, response.getArchiveId());

            verify(applicationDetailRepository).save(detailCaptor.capture());
            ApplicationDetail saved = detailCaptor.getValue();
            assertEquals(1L, saved.getApplicationId());
            assertEquals(100L, saved.getArchiveId());
        }

        @Test
        @DisplayName("申请单 ID 为空时抛出异常")
        void createDetail_nullApplicationId_throws() {
            ApplicationDetailCreateRequest request = new ApplicationDetailCreateRequest(null, 100L);

            assertThrows(IllegalArgumentException.class, () -> detailService.createDetail(request));
            verify(applicationDetailRepository, never()).save(any());
        }

        @Test
        @DisplayName("档案 ID 为空时抛出异常")
        void createDetail_nullArchiveId_throws() {
            ApplicationDetailCreateRequest request = new ApplicationDetailCreateRequest(1L, null);

            assertThrows(IllegalArgumentException.class, () -> detailService.createDetail(request));
            verify(applicationDetailRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("更新申请单明细")
    class UpdateDetailTests {

        @Test
        @DisplayName("更新存在的明细并返回 ApplicationDetailResponse")
        void updateDetail_success() {
            // given
            ApplicationDetail existing = new ApplicationDetail(1L, 100L);
            existing.setId(1L);

            when(applicationDetailRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(applicationDetailRepository.update(any(ApplicationDetail.class))).thenAnswer(inv -> inv.getArgument(0));

            ApplicationDetailUpdateRequest request = new ApplicationDetailUpdateRequest(1L, 2L, 200L);

            // when
            ApplicationDetailResponse response = detailService.updateDetail(request);

            // then
            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals(2L, response.getApplicationId());
            assertEquals(200L, response.getArchiveId());

            verify(applicationDetailRepository).update(detailCaptor.capture());
            ApplicationDetail updated = detailCaptor.getValue();
            assertEquals(2L, updated.getApplicationId());
            assertEquals(200L, updated.getArchiveId());
        }

        @Test
        @DisplayName("明细不存在时抛出异常")
        void updateDetail_notFound_throws() {
            when(applicationDetailRepository.findById(99L)).thenReturn(Optional.empty());

            ApplicationDetailUpdateRequest request = new ApplicationDetailUpdateRequest(99L, 1L, 100L);

            assertThrows(RuntimeException.class, () -> detailService.updateDetail(request));
            verify(applicationDetailRepository, never()).update(any());
        }
    }

    @Nested
    @DisplayName("查询/分页")
    class QueryTests {

        @Test
        @DisplayName("分页查询返回 ApplicationDetailResponse 列表")
        void queryDetails_returnsConvertedPage() {
            // given
            Page<ApplicationDetail> page = new Page<>(1, 10);
            ApplicationDetail detail = new ApplicationDetail(1L, 100L);
            detail.setId(10L);
            page.setRecords(List.of(detail));

            when(applicationDetailRepository.query(any(ApplicationDetailQueryRequest.class), eq(1), eq(10))).thenReturn(page);

            ApplicationDetailQueryRequest query = new ApplicationDetailQueryRequest(null, null);

            // when
            IPage<ApplicationDetailResponse> res = detailService.queryDetails(query, 1, 10);

            // then
            assertNotNull(res);
            assertEquals(1, res.getRecords().size());
            assertEquals(10L, res.getRecords().get(0).getId());
            assertEquals(1L, res.getRecords().get(0).getApplicationId());
        }

        @Test
        @DisplayName("根据 ID 查询返回 ApplicationDetailResponse")
        void getDetailById_returnsResponse() {
            ApplicationDetail detail = new ApplicationDetail(1L, 100L);
            detail.setId(5L);
            when(applicationDetailRepository.findById(5L)).thenReturn(Optional.of(detail));

            ApplicationDetailResponse response = detailService.getDetailById(5L);

            assertNotNull(response);
            assertEquals(5L, response.getId());
            assertEquals(1L, response.getApplicationId());
            assertEquals(100L, response.getArchiveId());
        }

        @Test
        @DisplayName("根据 ID 查询不存在时返回 null")
        void getDetailById_notFound_returnsNull() {
            when(applicationDetailRepository.findById(99L)).thenReturn(Optional.empty());
            assertNull(detailService.getDetailById(99L));
        }
    }

    @Nested
    @DisplayName("删除")
    class DeleteTests {

        @Test
        @DisplayName("明细不存在时返回 false")
        void deleteDetail_notFound_returnsFalse() {
            when(applicationDetailRepository.findById(99L)).thenReturn(Optional.empty());
            assertFalse(detailService.deleteDetail(99L));
        }

        @Test
        @DisplayName("删除存在的明细，设置 deletedAt 并返回 true")
        void deleteDetail_success() {
            ApplicationDetail detail = new ApplicationDetail(1L, 100L);
            detail.setId(10L);
            when(applicationDetailRepository.findById(10L)).thenReturn(Optional.of(detail));
            when(applicationDetailRepository.update(any(ApplicationDetail.class))).thenAnswer(inv -> inv.getArgument(0));

            boolean result = detailService.deleteDetail(10L);
            assertTrue(result);

            verify(applicationDetailRepository).update(detailCaptor.capture());
            ApplicationDetail updated = detailCaptor.getValue();
            assertNotNull(updated.getDeletedAt());
        }

        @Test
        @DisplayName("批量删除直接调用 repository.softDeleteBatch")
        void batchDeleteDetails_callsSoftDeleteBatch() {
            List<Long> ids = List.of(1L, 2L);
            doNothing().when(applicationDetailRepository).softDeleteBatch(ids);

            boolean result = detailService.batchDeleteDetails(ids);
            assertTrue(result);

            verify(applicationDetailRepository).softDeleteBatch(ids);
        }
    }
}
