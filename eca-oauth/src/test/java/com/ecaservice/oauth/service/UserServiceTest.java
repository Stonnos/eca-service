package com.ecaservice.oauth.service;

import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.TestHelperUtils;
import com.ecaservice.oauth.config.CommonConfig;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.mapping.RoleMapperImpl;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.oauth.mapping.UserMapperImpl;
import com.ecaservice.oauth.repository.RoleRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import java.util.Collections;

import static com.ecaservice.oauth.TestHelperUtils.createRoleEntity;
import static com.ecaservice.oauth.entity.UserEntity_.CREATION_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link UserService} functionality.
 *
 * @author Roman Batygin
 */
@Import({CommonConfig.class, UserMapperImpl.class, RoleMapperImpl.class})
public class UserServiceTest extends AbstractJpaTest {

    private static final String PASSWORD = "pa66word!";

    @Inject
    private CommonConfig commonConfig;
    @Inject
    private UserMapper userMapper;
    @Inject
    private UserEntityRepository userEntityRepository;
    @Inject
    private RoleRepository roleRepository;

    private UserService userService;

    @Override
    public void init() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        userService = new UserService(commonConfig, passwordEncoder, userMapper, userEntityRepository, roleRepository);
    }

    @Override
    public void deleteAll() {
        userEntityRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testCreateUser() {
        roleRepository.save(createRoleEntity());
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        UserEntity userEntity = userService.createUser(createUserDto, PASSWORD);
        UserEntity actual = userEntityRepository.findById(userEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getLogin()).isEqualTo(createUserDto.getLogin());
    }

    @Test
    void testCreateUserWithNotExistingRole() {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        assertThrows(IllegalStateException.class, () -> userService.createUser(createUserDto, PASSWORD));
    }

    @Test
    public void testGetUsersPage() {
        roleRepository.save(createRoleEntity());
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        UserEntity userEntity = userService.createUser(createUserDto, PASSWORD);
        PageRequestDto pageRequestDto =
                new PageRequestDto(0, 10, CREATION_DATE, true, userEntity.getLogin(), Collections.emptyList());
        Page<UserEntity> usersPage = userService.getNextPage(pageRequestDto);
        assertThat(usersPage).isNotNull();
        assertThat(usersPage.getContent()).hasSize(1);
    }
}
