package com.ecaservice.oauth;

import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.dto.UpdateUserInfoDto;
import com.ecaservice.oauth.dto.UpdateUserNotificationEventOptionsDto;
import com.ecaservice.oauth.dto.UpdateUserNotificationOptionsDto;
import com.ecaservice.oauth.entity.ChangeEmailRequestEntity;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserNotificationEventOptionsEntity;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import com.ecaservice.user.model.Role;
import com.ecaservice.user.profile.options.dto.UserNotificationEventType;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.Collections;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

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
    private static final String PASSWORD = "pa66word!";
    private static final String TOKEN = "token";
    private static final String MIDDLE_NAME = "Igorevich";
    private static final String LAST_NAME = "Batygin";
    private static final String UPDATE_MIDDLE_NAME = "Иванович";
    private static final String UPDATE_LAST_NAME = "Иванов";
    private static final String UPDATE_FIRST_NAME = "Иван";

    private static final int PAGE_SIZE = 10;
    private static final int PAGE_NUMBER = 0;

    /**
     * Creates page request dto.
     *
     * @return page request dto
     */
    public static PageRequestDto createPageRequestDto() {
        return new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, null, true, null, Collections.emptyList());
    }

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
     * @param token - token value
     * @return change password request entity
     */
    public static ChangePasswordRequestEntity createChangePasswordRequestEntity(String token) {
        ChangePasswordRequestEntity changePasswordRequestEntity = new ChangePasswordRequestEntity();
        changePasswordRequestEntity.setToken(md5Hex(token));
        changePasswordRequestEntity.setUserEntity(createUserEntity());
        return changePasswordRequestEntity;
    }

    /**
     * Creates change email request entity.
     *
     * @param token - token value
     * @return change email request entity
     */
    public static ChangeEmailRequestEntity createChangeEmailRequestEntity(String token) {
        ChangeEmailRequestEntity changePasswordRequestEntity = new ChangeEmailRequestEntity();
        changePasswordRequestEntity.setToken(md5Hex(token));
        changePasswordRequestEntity.setUserEntity(createUserEntity());
        return changePasswordRequestEntity;
    }

    /**
     * Create user profile options entity.
     *
     * @param userEntity - user entity
     * @return user profile options entity
     */
    public static UserProfileOptionsEntity createUserProfileOptionsEntity(UserEntity userEntity) {
        UserProfileOptionsEntity userProfileOptionsEntity = new UserProfileOptionsEntity();
        userProfileOptionsEntity.setEmailEnabled(true);
        userProfileOptionsEntity.setWebPushEnabled(true);
        userProfileOptionsEntity.setUserEntity(userEntity);
        userProfileOptionsEntity.setNotificationEventOptions(
                Collections.singletonList(createUserNotificationEventOptionsEntity()));
        userProfileOptionsEntity.setVersion(0);
        userProfileOptionsEntity.setCreated(LocalDateTime.now());
        return userProfileOptionsEntity;
    }

    /**
     * Creates user notification event options entity.
     *
     * @return user notification event options entity
     */
    public static UserNotificationEventOptionsEntity createUserNotificationEventOptionsEntity() {
        var userNotificationEventOptionsEntity = new UserNotificationEventOptionsEntity();
        userNotificationEventOptionsEntity.setEventType(UserNotificationEventType.EXPERIMENT_STATUS_CHANGE);
        userNotificationEventOptionsEntity.setEmailSupported(true);
        userNotificationEventOptionsEntity.setWebPushSupported(true);
        userNotificationEventOptionsEntity.setEmailEnabled(true);
        userNotificationEventOptionsEntity.setEmailSupported(true);
        return userNotificationEventOptionsEntity;
    }

    /**
     * Creates update user notification options.
     *
     * @return update user notification options
     */
    public static UpdateUserNotificationOptionsDto createUpdateUserNotificationOptionsDto() {
        var userNotificationOptionsDto =
                new UpdateUserNotificationOptionsDto();
        userNotificationOptionsDto.setWebPushEnabled(false);
        userNotificationOptionsDto.setEmailEnabled(false);
        userNotificationOptionsDto.setNotificationEventOptions(newArrayList());
        userNotificationOptionsDto.getNotificationEventOptions().add(
                createUpdateUserNotificationEventOptionsDto(UserNotificationEventType.EXPERIMENT_STATUS_CHANGE));
        userNotificationOptionsDto.getNotificationEventOptions().add(
                createUpdateUserNotificationEventOptionsDto(UserNotificationEventType.CLASSIFIER_STATUS_CHANGE));
        return userNotificationOptionsDto;
    }

    /**
     * Creates update user notification event options dto.
     *
     * @param eventType - event type
     * @return update user notification event options dto
     */
    public static UpdateUserNotificationEventOptionsDto createUpdateUserNotificationEventOptionsDto(
            UserNotificationEventType eventType) {
        var updateUserNotificationEventOptionsDto = new UpdateUserNotificationEventOptionsDto();
        updateUserNotificationEventOptionsDto.setEventType(eventType);
        updateUserNotificationEventOptionsDto.setEmailEnabled(false);
        updateUserNotificationEventOptionsDto.setWebPushEnabled(false);
        return updateUserNotificationEventOptionsDto;
    }
}
