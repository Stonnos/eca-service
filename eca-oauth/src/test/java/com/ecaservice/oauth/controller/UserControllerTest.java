package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.TestHelperUtils;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.dto.UpdateUserInfoDto;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserPhoto;
import com.ecaservice.oauth.mapping.RoleMapperImpl;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.oauth.mapping.UserMapperImpl;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.repository.UserPhotoRepository;
import com.ecaservice.oauth.service.PasswordService;
import com.ecaservice.oauth.service.UserService;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MimeTypeUtils;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ecaservice.oauth.TestHelperUtils.createPageRequestDto;
import static com.ecaservice.oauth.TestHelperUtils.createUpdateUserInfoDto;
import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static com.ecaservice.oauth.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.oauth.util.FieldConstraints.LOGIN_MAX_LENGTH;
import static com.ecaservice.oauth.util.FieldConstraints.LOGIN_MIN_LENGTH;
import static com.ecaservice.oauth.util.FieldConstraints.PERSON_NAME_MAX_SIZE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link UserController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = UserController.class)
@Import({UserMapperImpl.class, RoleMapperImpl.class})
class UserControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/users";
    private static final String CREATE_URL = BASE_URL + "/create";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String DOWNLOAD_PHOTO_URL = BASE_URL + "/photo/{id}";
    private static final String LOCK_URL = BASE_URL + "/lock";
    private static final String UNLOCK_URL = BASE_URL + "/unlock";
    private static final String GET_USER_INFO_URL = BASE_URL + "/user-info";
    private static final String LOGOUT_URL = BASE_URL + "/logout";
    private static final String TFA_ENABLED_URL = BASE_URL + "/tfa";
    private static final String UPDATE_USER_INFO = BASE_URL + "/update-info";
    private static final String DELETE_PHOTO_URL = BASE_URL + "/delete-photo";
    private static final String UPLOAD_PHOTO_URL = BASE_URL + "/upload-photo";

    private static final long TOTAL_ELEMENTS = 1L;
    private static final int PAGE_NUMBER = 0;
    private static final long PHOTO_ID = 1L;
    private static final String PHOTO_PNG = "photo.png";
    private static final int CONTENT_LENGTH = 32;
    private static final long USER_ID = 1L;
    private static final String USER_ID_PARAM = "userId";
    private static final long LOCK_USER_ID = 2L;
    private static final String TFA_ENABLED_PARAM = "enabled";
    private static final String INVALID_PERSON_DATA = "ивfd";

    private final MockMultipartFile photoFile =
            new MockMultipartFile("file", "photo.jpg",
                    MimeTypeUtils.TEXT_PLAIN.toString(), "file-content".getBytes(StandardCharsets.UTF_8));

    @MockBean
    private UserService userService;
    @MockBean
    private PasswordService passwordService;
    @Inject
    private UserMapper userMapper;
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;
    @MockBean
    private UserEntityRepository userEntityRepository;
    @MockBean
    private UserPhotoRepository userPhotoRepository;

    @Test
    void testCreateUserUnauthorized() throws Exception {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        mockMvc.perform(post(CREATE_URL)
                .content(objectMapper.writeValueAsString(createUserDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateUser() throws Exception {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        UserEntity userEntity = createUserEntity();
        UserDto expected = userMapper.map(userEntity);
        when(userService.createUser(any(CreateUserDto.class), any())).thenReturn(userEntity);
        mockMvc.perform(post(CREATE_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(createUserDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testCreateUserWithLoginSizeGreaterThanMaximum() throws Exception {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        createUserDto.setLogin(StringUtils.repeat('Q', LOGIN_MAX_LENGTH + 1));
        testCreateUserBadRequest(createUserDto);
    }

    @Test
    void testCreateUserWithLoginSizeLessThanMinimum() throws Exception {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        createUserDto.setLogin(StringUtils.repeat('Q', LOGIN_MIN_LENGTH - 1));
        testCreateUserBadRequest(createUserDto);
    }

    @Test
    void testCreateUserWithEmptyLogin() throws Exception {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        createUserDto.setLogin(StringUtils.EMPTY);
        testCreateUserBadRequest(createUserDto);
    }

    @Test
    void testCreateUserWithEmptyEmail() throws Exception {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        createUserDto.setEmail(StringUtils.EMPTY);
        testCreateUserBadRequest(createUserDto);
    }

    @Test
    void testCreateUserWithEmptyFirstName() throws Exception {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        createUserDto.setFirstName(StringUtils.EMPTY);
        testCreateUserBadRequest(createUserDto);
    }

    @Test
    void testCreateUserWithEmptyLastName() throws Exception {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        createUserDto.setLastName(StringUtils.EMPTY);
        testCreateUserBadRequest(createUserDto);
    }

    @Test
    void testCreateUserWithEmptyMiddleName() throws Exception {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        createUserDto.setMiddleName(StringUtils.EMPTY);
        testCreateUserBadRequest(createUserDto);
    }

    @Test
    void testCreateUserWithFirstNameGreaterThanMaxSize() throws Exception {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        String invalidFirstName = String.format("Q%s", StringUtils.repeat('q', PERSON_NAME_MAX_SIZE));
        createUserDto.setFirstName(invalidFirstName);
        testCreateUserBadRequest(createUserDto);
    }

    @Test
    void testCreateUserWithInvalidFirstNamePattern() throws Exception {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        String invalidFirstName = StringUtils.repeat('q', PERSON_NAME_MAX_SIZE);
        createUserDto.setFirstName(invalidFirstName);
        testCreateUserBadRequest(createUserDto);
    }

    @Test
    void testCreateUserWithInvalidEmail() throws Exception {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        createUserDto.setEmail(StringUtils.repeat('q', EMAIL_MAX_SIZE));
        testCreateUserBadRequest(createUserDto);
    }

    @Test
    void testGetUsersPageUnauthorized() throws Exception {
        mockMvc.perform(post(LIST_URL)
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUsersPage() throws Exception {
        Page<UserEntity> userEntityPage = Mockito.mock(Page.class);
        when(userEntityPage.getTotalElements()).thenReturn(TOTAL_ELEMENTS);
        List<UserEntity> userEntityList = Collections.singletonList(createUserEntity());
        PageDto<UserDto> expected = PageDto.of(userMapper.map(userEntityList), PAGE_NUMBER, TOTAL_ELEMENTS);
        when(userEntityPage.getContent()).thenReturn(userEntityList);
        when(userService.getNextPage(any(PageRequestDto.class))).thenReturn(userEntityPage);
        mockMvc.perform(post(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testDownloadUserPhotoUnauthorized() throws Exception {
        mockMvc.perform(get(DOWNLOAD_PHOTO_URL, PHOTO_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDownloadUserPhotoNotFound() throws Exception {
        when(userPhotoRepository.findById(PHOTO_ID)).thenReturn(Optional.empty());
        mockMvc.perform(get(DOWNLOAD_PHOTO_URL, PHOTO_ID)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDownloadUserPhoto() throws Exception {
        UserPhoto userPhoto = new UserPhoto();
        userPhoto.setFileName(PHOTO_PNG);
        userPhoto.setPhoto(new byte[CONTENT_LENGTH]);
        when(userPhotoRepository.findById(PHOTO_ID)).thenReturn(Optional.of(userPhoto));
        mockMvc.perform(get(DOWNLOAD_PHOTO_URL, PHOTO_ID)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().bytes(userPhoto.getPhoto()))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }

    @Test
    void testLockUserUnauthorized() throws Exception {
        mockMvc.perform(post(LOCK_URL)
                .param(USER_ID_PARAM, String.valueOf(USER_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLockUserWithNullUserIdParam() throws Exception {
        mockMvc.perform(post(LOCK_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLockYourself() throws Exception {
        mockMvc.perform(post(LOCK_URL)
                .param(USER_ID_PARAM, String.valueOf(USER_ID))
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testLockUser() throws Exception {
        mockMvc.perform(post(LOCK_URL)
                .param(USER_ID_PARAM, String.valueOf(LOCK_USER_ID))
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk());
        verify(userService, atLeastOnce()).lock(LOCK_USER_ID);
    }

    @Test
    void testUnlockUserUnauthorized() throws Exception {
        mockMvc.perform(post(UNLOCK_URL)
                .param(USER_ID_PARAM, String.valueOf(USER_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUnlockUserWithNullUserIdParam() throws Exception {
        mockMvc.perform(post(UNLOCK_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUnlockUser() throws Exception {
        mockMvc.perform(post(UNLOCK_URL)
                .param(USER_ID_PARAM, String.valueOf(USER_ID))
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk());
        verify(userService, atLeastOnce()).unlock(USER_ID);
    }

    @Test
    void testGetUserInfoUnauthorized() throws Exception {
        mockMvc.perform(get(GET_USER_INFO_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserInfoOk() throws Exception {
        UserEntity userEntity = createUserEntity();
        userEntity.setId(USER_ID);
        UserDto expected = userMapper.map(userEntity);
        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(userPhotoRepository.getUserPhotoId(userEntity)).thenReturn(null);
        mockMvc.perform(get(GET_USER_INFO_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testLogoutUnauthorized() throws Exception {
        mockMvc.perform(post(LOGOUT_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post(LOGOUT_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk());
    }

    @Test
    void testTfaEnabledUnauthorized() throws Exception {
        mockMvc.perform(post(TFA_ENABLED_URL)
                .param(TFA_ENABLED_PARAM, Boolean.TRUE.toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testTfaEnabled() throws Exception {
        mockMvc.perform(post(TFA_ENABLED_URL)
                .param(TFA_ENABLED_PARAM, Boolean.TRUE.toString())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk());
        verify(userService, atLeastOnce()).enableTfa(USER_ID);
    }

    @Test
    void testTfaDisabled() throws Exception {
        mockMvc.perform(post(TFA_ENABLED_URL)
                .param(TFA_ENABLED_PARAM, Boolean.FALSE.toString())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk());
        verify(userService, atLeastOnce()).disableTfa(USER_ID);
    }

    @Test
    void testUpdateUserInfoUnauthorized() throws Exception {
        var updateUserInfo = createUpdateUserInfoDto();
        mockMvc.perform(put(UPDATE_USER_INFO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserInfo)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUserWithEmptyFirstName() throws Exception {
        var updateUserInfo = createUpdateUserInfoDto();
        updateUserInfo.setFirstName(StringUtils.EMPTY);
        testUpdateUserInfoBadRequest(updateUserInfo);
    }

    @Test
    void testUpdateUserWithEmptyLastName() throws Exception {
        var updateUserInfo = createUpdateUserInfoDto();
        updateUserInfo.setLastName(StringUtils.EMPTY);
        testUpdateUserInfoBadRequest(updateUserInfo);
    }

    @Test
    void testUpdateUserWithEmptyMiddleName() throws Exception {
        var updateUserInfo = createUpdateUserInfoDto();
        updateUserInfo.setMiddleName(StringUtils.EMPTY);
        testUpdateUserInfoBadRequest(updateUserInfo);
    }

    @Test
    void testUpdateUserWithLargeFirstName() throws Exception {
        var updateUserInfo = createUpdateUserInfoDto();
        updateUserInfo.setFirstName(StringUtils.repeat('Q', PERSON_NAME_MAX_SIZE + 1));
        testUpdateUserInfoBadRequest(updateUserInfo);
    }

    @Test
    void testUpdateUserWithLargeLastName() throws Exception {
        var updateUserInfo = createUpdateUserInfoDto();
        updateUserInfo.setLastName(StringUtils.repeat('Q', PERSON_NAME_MAX_SIZE + 1));
        testUpdateUserInfoBadRequest(updateUserInfo);
    }

    @Test
    void testUpdateUserWithLargeMiddleName() throws Exception {
        var updateUserInfo = createUpdateUserInfoDto();
        updateUserInfo.setMiddleName(StringUtils.repeat('Q', PERSON_NAME_MAX_SIZE + 1));
        testUpdateUserInfoBadRequest(updateUserInfo);
    }

    @Test
    void testUpdateUserWithInvalidFirstName() throws Exception {
        var updateUserInfo = createUpdateUserInfoDto();
        updateUserInfo.setFirstName(INVALID_PERSON_DATA);
        testUpdateUserInfoBadRequest(updateUserInfo);
    }

    @Test
    void testUpdateUserWithInvalidLastName() throws Exception {
        var updateUserInfo = createUpdateUserInfoDto();
        updateUserInfo.setLastName(INVALID_PERSON_DATA);
        testUpdateUserInfoBadRequest(updateUserInfo);
    }

    @Test
    void testUpdateUserWithInvalidMiddleName() throws Exception {
        var updateUserInfo = createUpdateUserInfoDto();
        updateUserInfo.setMiddleName(INVALID_PERSON_DATA);
        testUpdateUserInfoBadRequest(updateUserInfo);
    }

    @Test
    void testUpdateUserInfoOk() throws Exception {
        var updateUserInfo = createUpdateUserInfoDto();
        mockMvc.perform(put(UPDATE_USER_INFO)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserInfo)))
                .andExpect(status().isOk());
        verify(userService, atLeastOnce()).updateUserInfo(USER_ID, updateUserInfo);
    }

    @Test
    void testDeletePhotoUnauthorized() throws Exception {
        mockMvc.perform(delete(DELETE_PHOTO_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeletePhoto() throws Exception {
        mockMvc.perform(delete(DELETE_PHOTO_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk());
        verify(userService, atLeastOnce()).deletePhoto(USER_ID);
    }

    @Test
    void testUploadPhotoUnauthorized() throws Exception {
        mockMvc.perform(multipart(UPLOAD_PHOTO_URL)
                .file(photoFile))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUploadPhotoWithNullFile() throws Exception {
        mockMvc.perform(multipart(UPLOAD_PHOTO_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUploadPhotoOk() throws Exception {
        mockMvc.perform(multipart(UPLOAD_PHOTO_URL)
                .file(photoFile)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk());
        verify(userService, atLeastOnce()).updatePhoto(USER_ID, photoFile);
    }

    private void testCreateUserBadRequest(CreateUserDto createUserDto) throws Exception {
        mockMvc.perform(post(CREATE_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(createUserDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private void testUpdateUserInfoBadRequest(UpdateUserInfoDto updateUserInfoDto) throws Exception {
        mockMvc.perform(put(UPDATE_USER_INFO)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserInfoDto)))
                .andExpect(status().isBadRequest());
    }
}
