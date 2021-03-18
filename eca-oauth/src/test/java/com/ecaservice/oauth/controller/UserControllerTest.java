package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.TestHelperUtils;
import com.ecaservice.oauth.dto.CreateUserDto;
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

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static com.ecaservice.oauth.util.FieldConstraints.EMAIL_MAX_SIZE;
import static com.ecaservice.oauth.util.FieldConstraints.LOGIN_MAX_LENGTH;
import static com.ecaservice.oauth.util.FieldConstraints.LOGIN_MIN_LENGTH;
import static com.ecaservice.oauth.util.FieldConstraints.PERSON_NAME_MAX_SIZE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";

    private static final long TOTAL_ELEMENTS = 1L;
    private static final int PAGE_NUMBER = 0;
    private static final long PHOTO_ID = 1L;
    private static final String PHOTO_PNG = "photo.png";
    private static final int CONTENT_LENGTH = 32;
    private static final long USER_ID = 1L;
    private static final String USER_ID_PARAM = "userId";

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
        mockMvc.perform(get(LIST_URL)
                .param(PAGE_PARAM, String.valueOf(PAGE_NUMBER))
                .param(SIZE_PARAM, String.valueOf(TOTAL_ELEMENTS)))
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
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .param(PAGE_PARAM, String.valueOf(PAGE_NUMBER))
                .param(SIZE_PARAM, String.valueOf(TOTAL_ELEMENTS)))
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
                .andExpect(status().isNotFound());
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
    void testLockUser() throws Exception {
        mockMvc.perform(post(LOCK_URL)
                .param(USER_ID_PARAM, String.valueOf(USER_ID))
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk());
        verify(userService, atLeastOnce()).lock(USER_ID);
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

    private void testCreateUserBadRequest(CreateUserDto createUserDto) throws Exception {
        mockMvc.perform(post(CREATE_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(createUserDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
