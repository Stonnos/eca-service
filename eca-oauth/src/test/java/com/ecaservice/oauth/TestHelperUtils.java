package com.ecaservice.oauth;

import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.dto.UpdateUserInfoDto;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.user.model.Role;
import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

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
    private static final String MIDDLE_NAME = "Igorevich";
    private static final String LAST_NAME = "Batygin";
    private static final String UPDATE_MIDDLE_NAME = "updateMiddleName";
    private static final String UPDATE_LAST_NAME = "updateLastName";
    private static final String UPDATE_FIRST_NAME = "updateFirstName";

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
     * Creates user entity.
     *
     * @return user entity
     */
    public static UserEntity createUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(USER_NAME);
        userEntity.setEmail(EMAIL);
        userEntity.setFirstName(FIRST_NAME);
        userEntity.setLastName(LAST_NAME);
        userEntity.setMiddleName(MIDDLE_NAME);
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
        return createUserDto(USER_NAME, EMAIL, FIRST_NAME, LAST_NAME, MIDDLE_NAME);
    }

    /**
     * Creates user dto.
     *
     * @param login      - user login
     * @param email      - user email
     * @param firstName  - user first name
     * @param lastName   - user last name
     * @param middleName - user middle name
     * @return create user dto
     */
    public static CreateUserDto createUserDto(String login, String email, String firstName, String lastName,
                                              String middleName) {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setLogin(login);
        createUserDto.setEmail(email);
        createUserDto.setFirstName(firstName);
        createUserDto.setLastName(lastName);
        createUserDto.setMiddleName(middleName);
        return createUserDto;
    }

    /**
     * Creates update user info dto.
     *
     * @return update user info dto
     */
    public static UpdateUserInfoDto createUpdateUserInfoDto() {
        UpdateUserInfoDto updateUserInfoDto = new UpdateUserInfoDto();
        updateUserInfoDto.setFirstName(UPDATE_FIRST_NAME);
        updateUserInfoDto.setLastName(UPDATE_LAST_NAME);
        updateUserInfoDto.setMiddleName(UPDATE_MIDDLE_NAME);
        return updateUserInfoDto;
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

    /**
     * Creates change password request entity.
     *
     * @return change password request entity
     */
    public static ChangePasswordRequestEntity createChangePasswordRequestEntity() {
        ChangePasswordRequestEntity changePasswordRequestEntity = new ChangePasswordRequestEntity();
        changePasswordRequestEntity.setToken(TOKEN);
        changePasswordRequestEntity.setUserEntity(createUserEntity());
        return changePasswordRequestEntity;
    }
}
