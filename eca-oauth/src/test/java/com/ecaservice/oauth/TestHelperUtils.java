package com.ecaservice.oauth;

import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.user.model.Role;
import com.ecaservice.user.model.UserDetailsImpl;
import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String USER_ROLE_DESCRIPTION = "Пользователь";
    private static final String USER_NAME = "user";
    private static final String EMAIL = "test@mail.ru";
    private static final String FIRST_NAME = "Roman";
    private static final long USER_ID = 1L;
    private static final String PASSWORD = "pa66word!";
    private static final String TOKEN = "token";

    /**
     * Creates role entity.
     *
     * @return role entity
     */
    public static RoleEntity createRoleEntity() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleName(Role.ROLE_ECA_USER);
        roleEntity.setDescription(USER_ROLE_DESCRIPTION);
        return roleEntity;
    }

    /**
     * Creates role.
     *
     * @return role
     */
    public static Role createRole() {
        Role role = new Role();
        role.setAuthority(Role.ROLE_ECA_USER);
        role.setDescription(USER_ROLE_DESCRIPTION);
        return role;
    }

    /**
     * Creates user details.
     *
     * @return user details
     */
    public static UserDetailsImpl createUserDetails() {
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setUserName(USER_NAME);
        userDetails.setEmail(EMAIL);
        userDetails.setFirstName(FIRST_NAME);
        userDetails.setId(USER_ID);
        userDetails.setAuthorities(Collections.singletonList(createRole()));
        userDetails.setCreationDate(LocalDateTime.now());
        userDetails.setTfaEnabled(true);
        return userDetails;
    }

    /**
     * Creates user entity.
     *
     * @return user entity
     */
    public static UserEntity createUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(USER_NAME);
        userEntity.setEmail(EMAIL);
        userEntity.setFirstName(FIRST_NAME);
        userEntity.setId(USER_ID);
        userEntity.setRoles(Sets.newHashSet(createRoleEntity()));
        userEntity.setPassword(PASSWORD);
        userEntity.setCreationDate(LocalDateTime.now());
        userEntity.setTfaEnabled(true);
        return userEntity;
    }

    /**
     * Creates user dto.
     *
     * @return create user dto
     */
    public static CreateUserDto createUserDto() {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setLogin(USER_NAME);
        createUserDto.setEmail(EMAIL);
        createUserDto.setFirstName(FIRST_NAME);
        return createUserDto;
    }

    /**
     * Creates reset password request entity.
     *
     * @return reset password request entity
     */
    public static ResetPasswordRequestEntity createResetPasswordRequestEntity() {
        ResetPasswordRequestEntity resetPasswordRequestEntity = new ResetPasswordRequestEntity();
        resetPasswordRequestEntity.setToken(TOKEN);
        resetPasswordRequestEntity.setUserEntity(createUserEntity());
        return resetPasswordRequestEntity;
    }
}
