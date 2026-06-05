package com.bosyon.zisnackdesk.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bosyon.zisnackdesk.mapper.SysUserMapper;
import com.bosyon.zisnackdesk.model.SysUser;
import com.bosyon.zisnackdesk.model.dto.SysUserCreateDTO;
import com.bosyon.zisnackdesk.model.dto.SysUserQueryDTO;
import com.bosyon.zisnackdesk.model.dto.SysUserUpdateDTO;
import com.bosyon.zisnackdesk.model.vo.SysUserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysUserServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private SysUserServiceImpl sysUserService;

    @Captor
    private ArgumentCaptor<SysUser> userCaptor;

    @BeforeEach
    void setUp() {
        // ServiceImpl 内部通过 baseMapper 调用 MyBatis-Plus 方法
        ReflectionTestUtils.setField(sysUserService, "baseMapper", sysUserMapper);
        // 使用 spy 包装 service，便于对 ServiceImpl 提供的方法（如 listByIds）进行存根
        sysUserService = spy(sysUserService);
    }

    @Nested
    @DisplayName("创建用户")
    class CreateUser {

        @Test
        @DisplayName("创建用户成功 - 使用默认 userType")
        void createUser_withDefaultUserType() {
            // given
            SysUserCreateDTO dto = new SysUserCreateDTO();
            dto.setAccount("test_user");
            dto.setMobile("13800138000");
            dto.setEmail("test@example.com");
            dto.setPassword("password123");

            when(sysUserMapper.insert(any(SysUser.class))).thenAnswer(invocation -> {
                SysUser user = invocation.getArgument(0);
                user.setId("mock-id-001");
                return 1;
            });

            // when
            SysUserVO result = sysUserService.createUser(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("mock-id-001");
            assertThat(result.getAccount()).isEqualTo("test_user");
            assertThat(result.getUserType()).isEqualTo("member"); // 默认值
            verify(sysUserMapper).insert(userCaptor.capture());
            assertThat(userCaptor.getValue().getUserType()).isEqualTo("member");
        }

        @Test
        @DisplayName("创建用户成功 - 使用自定义 userType")
        void createUser_withCustomUserType() {
            // given
            SysUserCreateDTO dto = new SysUserCreateDTO();
            dto.setAccount("admin_user");
            dto.setMobile("13900139000");
            dto.setPassword("admin123");
            dto.setUserType("admin");

            when(sysUserMapper.insert(any(SysUser.class))).thenAnswer(invocation -> {
                SysUser user = invocation.getArgument(0);
                user.setId("mock-id-002");
                return 1;
            });

            // when
            SysUserVO result = sysUserService.createUser(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("mock-id-002");
            assertThat(result.getUserType()).isEqualTo("admin");
        }
    }

    @Nested
    @DisplayName("更新用户")
    class UpdateUser {

        @Test
        @DisplayName("更新用户成功")
        void updateUser_success() {
            // given
            SysUser existingUser = new SysUser();
            existingUser.setId("user-001");
            existingUser.setAccount("old_name");
            existingUser.setMobile("13800138000");

            SysUserUpdateDTO dto = new SysUserUpdateDTO();
            dto.setId("user-001");
            dto.setAccount("new_name");
            dto.setMobile("13900139000");

            when(sysUserMapper.selectById("user-001")).thenReturn(existingUser);
            when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

            // when
            SysUserVO result = sysUserService.updateUser(dto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getAccount()).isEqualTo("new_name");
            assertThat(result.getMobile()).isEqualTo("13900139000");
            verify(sysUserMapper).updateById(userCaptor.capture());
            assertThat(userCaptor.getValue().getAccount()).isEqualTo("new_name");
        }

        @Test
        @DisplayName("更新用户 - 用户不存在抛出异常")
        void updateUser_notFound() {
            // given
            SysUserUpdateDTO dto = new SysUserUpdateDTO();
            dto.setId("non-existent");

            when(sysUserMapper.selectById("non-existent")).thenReturn(null);

            // when & then
            assertThatThrownBy(() -> sysUserService.updateUser(dto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("用户不存在");
        }
    }

    @Nested
    @DisplayName("查询用户")
    class GetUser {

        @Test
        @DisplayName("根据 ID 获取用户 VO - 存在")
        void getUserVOById_found() {
            // given
            SysUser user = new SysUser();
            user.setId("user-001");
            user.setAccount("test_user");
            user.setMobile("13800138000");
            user.setEmail("test@example.com");
            user.setUserType("member");

            when(sysUserMapper.selectById("user-001")).thenReturn(user);

            // when
            SysUserVO result = sysUserService.getUserVOById("user-001");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("user-001");
            assertThat(result.getAccount()).isEqualTo("test_user");
            assertThat(result.getMobile()).isEqualTo("13800138000");
            assertThat(result.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("根据 ID 获取用户 VO - 不存在返回 null")
        void getUserVOById_notFound() {
            when(sysUserMapper.selectById(anyString())).thenReturn(null);

            SysUserVO result = sysUserService.getUserVOById("non-existent");

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("分页条件查询")
    class QueryUsers {

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("分页查询 - 带筛选条件")
        void queryUsers_withConditions() {
            // given
            SysUserQueryDTO queryDTO = new SysUserQueryDTO();
            queryDTO.setAccount("test");
            queryDTO.setUserType("member");

            SysUser user = new SysUser();
            user.setId("user-001");
            user.setAccount("test_user");
            user.setUserType("member");

            Page<SysUser> pageResult = new Page<>(1, 10, 1);
            pageResult.setRecords(List.of(user));

            when(sysUserMapper.selectPage(any(Page.class), any())).thenReturn(pageResult);

            // when
            IPage<SysUserVO> result = sysUserService.queryUsers(queryDTO, 1, 10);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).hasSize(1);
            assertThat(result.getRecords().get(0).getAccount()).isEqualTo("test_user");
            assertThat(result.getTotal()).isEqualTo(1);
            verify(sysUserMapper).selectPage(any(Page.class), any());
        }

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("分页查询 - 无条件")
        void queryUsers_noConditions() {
            // given
            SysUserQueryDTO queryDTO = new SysUserQueryDTO();

            Page<SysUser> pageResult = new Page<>(1, 10, 0);

            when(sysUserMapper.selectPage(any(Page.class), any())).thenReturn(pageResult);

            // when
            IPage<SysUserVO> result = sysUserService.queryUsers(queryDTO, 1, 10);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getRecords()).isEmpty();
            verify(sysUserMapper).selectPage(any(Page.class), any());
        }
    }

    @Nested
    @DisplayName("删除用户")
    class DeleteUser {

        @Test
        @DisplayName("软删除用户成功")
        void deleteUser_success() {
            // given
            SysUser user = new SysUser();
            user.setId("user-001");
            user.setDeletedAt(null);

            when(sysUserMapper.selectById("user-001")).thenReturn(user);
            when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

            // when
            boolean result = sysUserService.deleteUser("user-001");

            // then
            assertThat(result).isTrue();
            verify(sysUserMapper).updateById(userCaptor.capture());
            assertThat(userCaptor.getValue().getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("软删除用户 - 不存在返回 false")
        void deleteUser_notFound() {
            when(sysUserMapper.selectById(anyString())).thenReturn(null);

            boolean result = sysUserService.deleteUser("non-existent");

            assertThat(result).isFalse();
            verify(sysUserMapper, never()).updateById((SysUser) any());
        }

        @Test
        @DisplayName("批量软删除 - ID 不存在返回 false")
        void batchDeleteUsers_empty() {
            // stub 服务层的 listByIds，避免对 mapper 的未使用存根
            doReturn(List.of()).when(sysUserService).listByIds(anyList());

            boolean result = sysUserService.batchDeleteUsers(List.of("non-existent"));

            assertThat(result).isFalse();
            verify(sysUserMapper, never()).updateById((SysUser) any());
        }

        @Test
        @DisplayName("批量软删除用户成功")
        void batchDeleteUsers_success() {
            // given
            SysUser user1 = new SysUser();
            user1.setId("user-001");
            SysUser user2 = new SysUser();
            user2.setId("user-002");

            // stub 服务层的 listByIds，令其返回两个用户对象
            doReturn(List.of(user1, user2)).when(sysUserService).listByIds(anyList());
            when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);

            // when
            boolean result = sysUserService.batchDeleteUsers(List.of("user-001", "user-002"));

            // then
            assertThat(result).isTrue();
            assertThat(user1.getDeletedAt()).isNotNull();
            assertThat(user2.getDeletedAt()).isNotNull();
            verify(sysUserMapper, times(2)).updateById((SysUser) any());
        }
    }
}
