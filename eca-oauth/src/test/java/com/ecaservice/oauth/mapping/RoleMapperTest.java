package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.web.dto.model.RoleDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.ecaservice.oauth.TestHelperUtils.createRoleEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link RoleMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(RoleMapperImpl.class)
class RoleMapperTest {

    @Autowired
    private RoleMapper roleMapper;

    @Test
    void testMapRoleEntityToRoleDto() {
        RoleEntity roleEntity = createRoleEntity();
        RoleDto roleDto = roleMapper.map(roleEntity);
        assertThat(roleDto).isNotNull();
        assertThat(roleDto.getRoleName()).isEqualTo(roleEntity.getRoleName());
        assertThat(roleDto.getDescription()).isEqualTo(roleEntity.getDescription());
    }
}
