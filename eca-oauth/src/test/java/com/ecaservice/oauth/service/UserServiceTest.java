package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.common.web.exception.InvalidFileException;
import com.ecaservice.common.web.exception.InvalidOperationException;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.TestHelperUtils;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.dto.UpdateUserInfoDto;
import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserEntity_;
import com.ecaservice.oauth.entity.UserPhoto;
import com.ecaservice.oauth.exception.UserLockNotAllowedException;
import com.ecaservice.oauth.exception.UserLockedException;
import com.ecaservice.oauth.mapping.RoleMapperImpl;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.oauth.mapping.UserMapperImpl;
import com.ecaservice.oauth.repository.RoleRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.repository.UserPhotoRepository;
import com.ecaservice.user.model.Role;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import com.ecaservice.web.dto.model.UserDto;
import com.google.common.collect.Sets;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.ecaservice.oauth.TestHelperUtils.createRoleEntity;
import static com.ecaservice.oauth.TestHelperUtils.createUpdateUserInfoDto;
import static com.ecaservice.oauth.TestHelperUtils.createUserDto;
import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static com.ecaservice.oauth.dictionary.FilterDictionaries.USERS_TEMPLATE;
import static com.ecaservice.oauth.entity.UserEntity_.CREATION_DATE;
import static com.ecaservice.oauth.entity.UserEntity_.FULL_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link UserService} functionality.
 *
 * @author Roman Batygin
 */
@Import({AppProperties.class, UserMapperImpl.class, RoleMapperImpl.class})
class UserServiceTest extends AbstractJpaTest {

    private static final String PASSWORD = "pa66word!";
    private static final String INVALID_USERNAME = "abc";
    private static final String PHOTO_FILE_NAME = "image.png";
    private static final int BYTE_ARRAY_LENGTH = 32;
    private static final int PAGE = 0;
    private static final int SIZE = 10;

    @Autowired
    private AppProperties appProperties;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserPhotoRepository userPhotoRepository;

    @MockBean
    private Oauth2RevokeTokenService oauth2RevokeTokenService;
    @MockBean
    private UserProfileOptionsConfigurationService userProfileOptionsConfigurationService;
    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private UserProfileOptionsDataEventService userProfileOptionsDataEventService;

    private UserService userService;

    @Override
    public void init() {
        roleRepository.save(createRoleEntity());
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        userService = new UserService(appProperties, passwordEncoder, userMapper, oauth2RevokeTokenService,
                userProfileOptionsConfigurationService, userProfileOptionsDataEventService, filterTemplateService,
                userEntityRepository, roleRepository, userPhotoRepository);
        when(filterTemplateService.getGlobalFilterFields(USERS_TEMPLATE)).thenReturn(
                List.of(UserEntity_.LOGIN,
                        UserEntity_.EMAIL,
                        UserEntity_.FULL_NAME)
        );
    }

    @Override
    public void deleteAll() {
        userEntityRepository.deleteAll();
        roleRepository.deleteAll();
        userPhotoRepository.deleteAll();
    }

    @Test
    void testCreateUser() {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        UserEntity userEntity = userService.createUser(createUserDto, PASSWORD);
        UserEntity actual = userEntityRepository.findById(userEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getLogin()).isEqualTo(createUserDto.getLogin());
        assertThat(actual.getPasswordChangeDate()).isNull();
        assertThat(actual.isForceChangePassword()).isTrue();
    }

    @Test
    void testCreateUserWithNotExistingRole() {
        roleRepository.deleteAll();
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        assertThrows(EntityNotFoundException.class, () -> userService.createUser(createUserDto, PASSWORD));
    }

    @Test
    void testUpdateUserInfo() {
        UserEntity userEntity = createAndSaveUser();
        UpdateUserInfoDto updateUserInfoDto = createUpdateUserInfoDto();
        userService.updateUserInfo(userEntity.getLogin(), updateUserInfoDto);
        UserEntity actual = userEntityRepository.findById(userEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getFirstName()).isEqualTo(updateUserInfoDto.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(updateUserInfoDto.getLastName());
        assertThat(actual.getMiddleName()).isEqualTo(updateUserInfoDto.getMiddleName());
    }

    @Test
    void testGetUserInfo() {
        UserEntity userEntity = createAndSaveUser();
        var userInfoDto = userService.getUserInfo(userEntity.getLogin());
        assertThat(userInfoDto).isNotNull();
        assertThat(userInfoDto.getLogin()).isEqualTo(userEntity.getLogin());
        assertThat(userInfoDto.getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(userInfoDto.getFirstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userInfoDto.getFirstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userInfoDto.getLastName()).isEqualTo(userEntity.getLastName());
        assertThat(userInfoDto.getMiddleName()).isEqualTo(userEntity.getMiddleName());
    }

    @Test
    void testGetUserDetails() {
        UserEntity userEntity = createAndSaveUser();
        var userDto = userService.getUserDetails(userEntity.getLogin());
        assertThat(userDto).isNotNull();
        assertThat(userDto.getLogin()).isEqualTo(userEntity.getLogin());
        assertThat(userDto.getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(userDto.getFirstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userDto.getFirstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userDto.getLastName()).isEqualTo(userEntity.getLastName());
        assertThat(userDto.getMiddleName()).isEqualTo(userEntity.getMiddleName());
    }

    @Test
    void testGetUsersPage() {
        UserEntity userEntity = createAndSaveUser();
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE, SIZE, Collections.singletonList(new SortFieldRequestDto(CREATION_DATE, true)),
                        userEntity.getLogin(), Collections.emptyList());
        Page<UserEntity> usersPage = userService.getNextPage(pageRequestDto);
        assertThat(usersPage).isNotNull();
        assertThat(usersPage.getContent()).hasSize(1);
    }

    @Test
    void testGetUsersDictionaryPage() {
        UserEntity userEntity = createAndSaveUser();
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE, SIZE, Collections.singletonList(new SortFieldRequestDto(CREATION_DATE, true)),
                        userEntity.getLogin(), Collections.emptyList());
        var dictionaryPage = userService.getUsersDictionaryPage(pageRequestDto);
        assertThat(dictionaryPage).isNotNull();
        assertThat(dictionaryPage.getContent()).hasSize(1);
    }

    @Test
    void testSortByFullName() {
        UserEntity first =
                userService.createUser(createUserDto("user1", "test1@mail.ru", "Ivan", "Ivanov", "Ivanovich"),
                        PASSWORD);
        UserEntity second =
                userService.createUser(createUserDto("user2", "test2@mail.ru", "Petr", "Babaev", "Petrovich"),
                        PASSWORD);
        UserEntity third =
                userService.createUser(createUserDto("user3", "test3@mail.ru", "Ivan", "Alaev", "Fedorovich"),
                        PASSWORD);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE, SIZE, Collections.singletonList(new SortFieldRequestDto(FULL_NAME, true)),
                        null, Collections.emptyList());
        var usersPage = userService.getUsersPage(pageRequestDto);
        assertThat(usersPage).isNotNull();
        assertThat(usersPage.getContent()).hasSize(3);
        Iterator<UserDto> iterator = usersPage.getContent().iterator();
        assertThat(iterator.next().getId()).isEqualTo(third.getId());
        assertThat(iterator.next().getId()).isEqualTo(second.getId());
        assertThat(iterator.next().getId()).isEqualTo(first.getId());
    }

    @Test
    void testSearchByFullName() {
        userService.createUser(createUserDto("user1", "test1@mail.ru", "Ivan", "Ivanov", "Petrovich"), PASSWORD);
        UserEntity second =
                userService.createUser(createUserDto("user2", "test2@mail.ru", "Petr", "Babaev", "Petrovich"),
                        PASSWORD);
        userService.createUser(createUserDto("user3", "test3@mail.ru", "Ivan", "Alaev", "Petrovich"), PASSWORD);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE, SIZE, Collections.singletonList(new SortFieldRequestDto(CREATION_DATE, true)),
                        "ev Petr Petr", Collections.emptyList());
        var usersPage = userService.getUsersPage(pageRequestDto);
        assertThat(usersPage).isNotNull();
        assertThat(usersPage.getContent()).hasSize(1);
        assertThat(usersPage.getContent().iterator().next().getId()).isEqualTo(second.getId());
    }

    @Test
    void testEnableTfa() {
        UserEntity userEntity = createAndSaveUser();
        userService.enableTfa(userEntity.getLogin());
        UserEntity actual = userEntityRepository.findById(userEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.isTfaEnabled()).isTrue();
    }

    @Test
    void testEnableTfaShouldThrowIllegalStateException() {
        UserEntity userEntity = createAndSaveUser();
        userEntity.setTfaEnabled(true);
        userEntityRepository.save(userEntity);
        assertThrows(InvalidOperationException.class, () -> userService.enableTfa(userEntity.getLogin()));
    }

    @Test
    void testEnableTfaForNotExistingUser() {
        assertThrows(EntityNotFoundException.class, () -> userService.enableTfa(INVALID_USERNAME));
    }

    @Test
    void testDisableTfa() {
        UserEntity userEntity = createAndSaveUser();
        userEntity.setTfaEnabled(true);
        userEntityRepository.save(userEntity);
        userService.disableTfa(userEntity.getLogin());
        UserEntity actual = userEntityRepository.findById(userEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.isTfaEnabled()).isFalse();
    }

    @Test
    void testDisableTfaShouldThrowIllegalStateException() {
        UserEntity userEntity = createAndSaveUser();
        assertThrows(InvalidOperationException.class, () -> userService.disableTfa(userEntity.getLogin()));
    }

    @Test
    void testDisableTfaForNotExistingUser() {
        assertThrows(EntityNotFoundException.class, () -> userService.disableTfa(INVALID_USERNAME));
    }

    @Test
    void testUpdatePhotoForNotExistingUser() {
        MultipartFile file = mock(MultipartFile.class);
        assertThrows(EntityNotFoundException.class, () -> userService.updatePhoto(INVALID_USERNAME, file));
    }

    @Test
    void testSaveNewPhoto() throws IOException {
        UserEntity userEntity = createAndSaveUser();
        savePhoto(userEntity);
        UserPhoto userPhoto = userPhotoRepository.findAll().iterator().next();
        assertThat(userPhoto).isNotNull();
        assertThat(userPhoto.getFileName()).isEqualTo(PHOTO_FILE_NAME);
        assertThat(userPhoto.getFileExtension()).isEqualTo(FilenameUtils.getExtension(PHOTO_FILE_NAME));
        assertThat(userPhoto.getPhoto()).isNotNull();
        assertThat(userPhoto.getPhoto()).hasSize(BYTE_ARRAY_LENGTH);
    }

    @Test
    void testUpdatePhoto() throws IOException {
        UserEntity userEntity = createAndSaveUser();
        savePhoto(userEntity);
        savePhoto(userEntity);
        var userPhotos = userPhotoRepository.findAll();
        assertThat(userPhotos).hasSize(1);
        UserPhoto userPhoto = userPhotos.iterator().next();
        assertThat(userPhoto).isNotNull();
        assertThat(userPhoto.getFileName()).isEqualTo(PHOTO_FILE_NAME);
        assertThat(userPhoto.getFileExtension()).isEqualTo(FilenameUtils.getExtension(PHOTO_FILE_NAME));
        assertThat(userPhoto.getPhoto()).isNotNull();
        assertThat(userPhoto.getPhoto()).hasSize(BYTE_ARRAY_LENGTH);
    }

    @Test
    void testUpdatePhotoWithInvalidExtension() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("file.txt");
        when(file.getBytes()).thenReturn(new byte[BYTE_ARRAY_LENGTH]);
        UserEntity userEntity = createAndSaveUser();
        assertThrows(InvalidFileException.class, () -> userService.updatePhoto(userEntity.getLogin(), file));
    }

    @Test
    void testDeletePhotoForNotExistingUser() {
        assertThrows(EntityNotFoundException.class, () -> userService.deletePhoto(INVALID_USERNAME));
    }

    @Test
    void testDeleteNotExistingPhoto() {
        UserEntity userEntity = createAndSaveUser();
        assertThrows(InvalidOperationException.class, () -> userService.deletePhoto(userEntity.getLogin()));
    }

    @Test
    void testDeletePhoto() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(PHOTO_FILE_NAME);
        when(file.getBytes()).thenReturn(new byte[BYTE_ARRAY_LENGTH]);
        UserEntity userEntity = createAndSaveUser();
        userService.updatePhoto(userEntity.getLogin(), file);
        userService.deletePhoto(userEntity.getLogin());
        assertThat(userPhotoRepository.findAll()).isEmpty();
    }

    @Test
    void testLockSuperAdmin() {
        UserEntity userEntity = createUserEntity();
        RoleEntity role = createRoleEntity();
        role.setRoleName(Role.ROLE_SUPER_ADMIN);
        roleRepository.save(role);
        userEntity.setRoles(Sets.newHashSet(role));
        userEntityRepository.save(userEntity);
        Long userId = userEntity.getId();
        assertThrows(UserLockNotAllowedException.class, () -> userService.lock(userId));
    }

    @Test
    void testLockUser() {
        UserEntity userEntity = createAndSaveUser();
        UserEntity locked = userService.lock(userEntity.getId());
        assertThat(locked).isNotNull();
        UserEntity actual = userEntityRepository.findById(userEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.isLocked()).isTrue();
        verify(oauth2RevokeTokenService, atLeastOnce()).revokeTokens(any(UserEntity.class));
    }

    @Test
    void testLockUserShouldThrowUserLockedException() {
        UserEntity userEntity = createAndSaveUser();
        userEntity.setLocked(true);
        userEntityRepository.save(userEntity);
        Long userId = userEntity.getId();
        assertThrows(UserLockedException.class, () -> userService.lock(userId));
    }

    @Test
    void testUnlockUser() {
        UserEntity userEntity = createAndSaveUser();
        userEntity.setLocked(true);
        userEntityRepository.save(userEntity);
        UserEntity unlocked = userService.unlock(userEntity.getId());
        assertThat(unlocked).isNotNull();
        UserEntity actual = userEntityRepository.findById(userEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.isLocked()).isFalse();
    }

    @Test
    void testUnlockUserShouldThrowInvalidOperationExceptionException() {
        UserEntity userEntity = createAndSaveUser();
        Long userId = userEntity.getId();
        assertThrows(InvalidOperationException.class, () -> userService.unlock(userId));
    }

    private void savePhoto(UserEntity userEntity) throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(PHOTO_FILE_NAME);
        when(file.getBytes()).thenReturn(new byte[BYTE_ARRAY_LENGTH]);
        userService.updatePhoto(userEntity.getLogin(), file);
    }

    private UserEntity createAndSaveUser() {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        return userService.createUser(createUserDto, PASSWORD);
    }
}
