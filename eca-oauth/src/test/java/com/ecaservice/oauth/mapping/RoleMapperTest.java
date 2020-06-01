package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.user.model.Role;
import com.ecaservice.web.dto.model.RoleDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.oauth.TestHelperUtils.createRole;
import static com.ecaservice.oauth.TestHelperUtils.createRoleEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link RoleMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(RoleMapperImpl.class)
public class RoleMapperTest {

    @Inject
    private RoleMapper roleMapper;

    @Test
    void testMapRoleEntityToRoleDto() {
        RoleEntity roleEntity = createRoleEntity();
        RoleDto roleDto = roleMapper.map(roleEntity);
        assertThat(roleDto).isNotNull();
        assertThat(roleDto.getRoleName()).isEqualTo(roleEntity.getRoleName());
        assertThat(roleDto.getDescription()).isEqualTo(roleEntity.getDescription());
    }

    @Test
    void testMapRoleToRoleDto() {
        Role role = createRole();
        RoleDto roleDto = roleMapper.map(role);
        assertThat(roleDto).isNotNull();
        assertThat(roleDto.getRoleName()).isEqualTo(role.getAuthority());
        assertThat(roleDto.getDescription()).isEqualTo(role.getDescription());
    }

    @Test
    void testMapRoleEntityToRole() {
        RoleEntity roleEntity = createRoleEntity();
        Role role = roleMapper.mapRole(roleEntity);
        assertThat(role).isNotNull();
        assertThat(role.getAuthority()).isEqualTo(roleEntity.getRoleName());
        assertThat(role.getDescription()).isEqualTo(roleEntity.getDescription());
    }
}
