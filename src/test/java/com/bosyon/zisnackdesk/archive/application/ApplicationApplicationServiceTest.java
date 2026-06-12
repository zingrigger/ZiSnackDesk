package com.bosyon.zisnackdesk.archive.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.archive.domain.Application;
import com.bosyon.zisnackdesk.archive.domain.ApplicationRepository;
import com.bosyon.zisnackdesk.archive.domain.ApplicationStatus;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationCreateRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationQueryRequest;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationResponse;
import com.bosyon.zisnackdesk.archive.interfaces.dto.ApplicationUpdateRequest;
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
@DisplayName("ApplicationApplicationService 单元测试")
class ApplicationApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private ApplicationApplicationService applicationService;

    @Captor
    private ArgumentCaptor<Application> applicationCaptor;

    @Nested
    @DisplayName("创建申请单")
    class CreateApplicationTests {

        @Test
        @DisplayName("创建申请单成功，返回 ApplicationResponse")
        void createApplication_success() {
            // given
            ApplicationCreateRequest request = new ApplicationCreateRequest(10L, ApplicationStatus.DRAFT.getCode());

            when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
                Application a = invocation.getArgument(0);
                a.setId(1L);
                a.setCreatedAt(LocalDateTime.now());
                return a;
            });

            // when
            ApplicationResponse response = applicationService.createApplication(request);

            // then
            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals(10L, response.getApplicantId());
            assertEquals(ApplicationStatus.DRAFT.getCode(), response.getStatus());

            verify(applicationRepository).save(applicationCaptor.capture());
            Application saved = applicationCaptor.getValue();
            assertEquals(10L, saved.getApplicantId());
            assertEquals(ApplicationStatus.DRAFT.getCode(), saved.getStatus());
        }

        @Test
        @DisplayName("申请人为空时抛出异常")
        void createApplication_nullApplicant_throws() {
            ApplicationCreateRequest request = new ApplicationCreateRequest(null, ApplicationStatus.DRAFT.getCode());

            assertThrows(IllegalArgumentException.class, () -> applicationService.createApplication(request));
            verify(applicationRepository, never()).save(any());
        }

        @Test
        @DisplayName("状态为空时抛出异常")
        void createApplication_nullStatus_throws() {
            ApplicationCreateRequest request = new ApplicationCreateRequest(10L, null);

            assertThrows(IllegalArgumentException.class, () -> applicationService.createApplication(request));
            verify(applicationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("更新申请单")
    class UpdateApplicationTests {

        @Test
        @DisplayName("更新存在的申请单并返回 ApplicationResponse")
        void updateApplication_success() {
            // given
            Application existing = new Application(10L, ApplicationStatus.DRAFT.getCode());
            existing.setId(1L);

            when(applicationRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(applicationRepository.update(any(Application.class))).thenAnswer(inv -> inv.getArgument(0));

            ApplicationUpdateRequest request = new ApplicationUpdateRequest(1L, 20L, ApplicationStatus.SUBMITTED.getCode());

            // when
            ApplicationResponse response = applicationService.updateApplication(request);

            // then
            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals(20L, response.getApplicantId());
            assertEquals(ApplicationStatus.SUBMITTED.getCode(), response.getStatus());

            verify(applicationRepository).update(applicationCaptor.capture());
            Application updated = applicationCaptor.getValue();
            assertEquals(20L, updated.getApplicantId());
            assertEquals(ApplicationStatus.SUBMITTED.getCode(), updated.getStatus());
        }

        @Test
        @DisplayName("申请单不存在时抛出异常")
        void updateApplication_notFound_throws() {
            when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

            ApplicationUpdateRequest request = new ApplicationUpdateRequest(99L, 10L, ApplicationStatus.DRAFT.getCode());

            assertThrows(RuntimeException.class, () -> applicationService.updateApplication(request));
            verify(applicationRepository, never()).update(any());
        }
    }

    @Nested
    @DisplayName("状态流转")
    class StateTransitionTests {

        @Test
        @DisplayName("已提交的申请单可以被批准")
        void approve_success() {
            Application app = new Application(10L, ApplicationStatus.DRAFT.getCode());
            app.setId(1L);
            // 先提交
            app.submit();
            when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));
            when(applicationRepository.update(any(Application.class))).thenAnswer(inv -> inv.getArgument(0));

            ApplicationUpdateRequest request = new ApplicationUpdateRequest(1L, 10L, ApplicationStatus.APPROVED.getCode());
            ApplicationResponse response = applicationService.updateApplication(request);

            assertNotNull(response);
            // updateApplication 会直接设置 status，因此这里验证的是 service 的行为
            assertEquals(ApplicationStatus.APPROVED.getCode(), response.getStatus());
        }

        @Test
        @DisplayName("只有草稿状态的申请单可以提交")
        void submit_fromNonDraft_throws() {
            Application app = new Application(10L, ApplicationStatus.APPROVED.getCode());
            app.setId(1L);

            assertThrows(IllegalStateException.class, app::submit);
        }

        @Test
        @DisplayName("只有已提交状态的申请单可以通过")
        void approve_fromNonSubmitted_throws() {
            Application app = new Application(10L, ApplicationStatus.DRAFT.getCode());
            app.setId(1L);

            assertThrows(IllegalStateException.class, app::approve);
        }

        @Test
        @DisplayName("只有已提交状态的申请单可以驳回")
        void reject_fromNonSubmitted_throws() {
            Application app = new Application(10L, ApplicationStatus.DRAFT.getCode());
            app.setId(1L);

            assertThrows(IllegalStateException.class, app::reject);
        }

        @Test
        @DisplayName("已提交状态可以成功驳回")
        void reject_success() {
            Application app = new Application(10L, ApplicationStatus.DRAFT.getCode());
            app.submit();
            assertEquals(ApplicationStatus.SUBMITTED.getCode(), app.getStatus());

            app.reject();
            assertEquals(ApplicationStatus.REJECTED.getCode(), app.getStatus());
        }
    }

    @Nested
    @DisplayName("查询/分页")
    class QueryTests {

        @Test
        @DisplayName("分页查询返回 ApplicationResponse 列表")
        void queryApplications_returnsConvertedPage() {
            // given
            Page<Application> page = new Page<>(1, 10);
            Application app = new Application(10L, ApplicationStatus.DRAFT.getCode());
            app.setId(5L);
            page.setRecords(List.of(app));

            when(applicationRepository.query(any(ApplicationQueryRequest.class), eq(1), eq(10))).thenReturn(page);

            ApplicationQueryRequest query = new ApplicationQueryRequest(null, null);

            // when
            IPage<ApplicationResponse> res = applicationService.queryApplications(query, 1, 10);

            // then
            assertNotNull(res);
            assertEquals(1, res.getRecords().size());
            assertEquals(5L, res.getRecords().get(0).getId());
            assertEquals(10L, res.getRecords().get(0).getApplicantId());
        }

        @Test
        @DisplayName("根据 ID 查询返回 ApplicationResponse")
        void getApplicationById_returnsResponse() {
            Application app = new Application(10L, ApplicationStatus.DRAFT.getCode());
            app.setId(3L);
            when(applicationRepository.findById(3L)).thenReturn(Optional.of(app));

            ApplicationResponse response = applicationService.getApplicationById(3L);

            assertNotNull(response);
            assertEquals(3L, response.getId());
            assertEquals(10L, response.getApplicantId());
        }

        @Test
        @DisplayName("根据 ID 查询不存在时返回 null")
        void getApplicationById_notFound_returnsNull() {
            when(applicationRepository.findById(99L)).thenReturn(Optional.empty());
            assertNull(applicationService.getApplicationById(99L));
        }
    }

    @Nested
    @DisplayName("删除")
    class DeleteTests {

        @Test
        @DisplayName("申请单不存在时返回 false")
        void deleteApplication_notFound_returnsFalse() {
            when(applicationRepository.findById(99L)).thenReturn(Optional.empty());
            assertFalse(applicationService.deleteApplication(99L));
        }

        @Test
        @DisplayName("删除存在的申请单，设置 deletedAt 并返回 true")
        void deleteApplication_success() {
            Application app = new Application(10L, ApplicationStatus.DRAFT.getCode());
            app.setId(5L);
            when(applicationRepository.findById(5L)).thenReturn(Optional.of(app));
            when(applicationRepository.update(any(Application.class))).thenAnswer(inv -> inv.getArgument(0));

            boolean result = applicationService.deleteApplication(5L);
            assertTrue(result);

            verify(applicationRepository).update(applicationCaptor.capture());
            Application updated = applicationCaptor.getValue();
            assertNotNull(updated.getDeletedAt());
        }

        @Test
        @DisplayName("批量删除直接调用 repository.softDeleteBatch")
        void batchDeleteApplications_callsSoftDeleteBatch() {
            List<Long> ids = List.of(1L, 2L);
            doNothing().when(applicationRepository).softDeleteBatch(ids);

            boolean result = applicationService.batchDeleteApplications(ids);
            assertTrue(result);

            verify(applicationRepository).softDeleteBatch(ids);
        }
    }
}
