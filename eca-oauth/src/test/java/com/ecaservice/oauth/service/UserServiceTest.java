package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.TestHelperUtils;
import com.ecaservice.oauth.config.CommonConfig;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserPhoto;
import com.ecaservice.oauth.exception.EmailDuplicationException;
import com.ecaservice.oauth.mapping.RoleMapperImpl;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.oauth.mapping.UserMapperImpl;
import com.ecaservice.oauth.repository.RoleRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.repository.UserPhotoRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

import static com.ecaservice.oauth.TestHelperUtils.createRoleEntity;
import static com.ecaservice.oauth.TestHelperUtils.createUserDto;
import static com.ecaservice.oauth.entity.UserEntity_.CREATION_DATE;
import static com.ecaservice.oauth.entity.UserEntity_.FULL_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link UserService} functionality.
 *
 * @author Roman Batygin
 */
@Import({CommonConfig.class, UserMapperImpl.class, RoleMapperImpl.class})
class UserServiceTest extends AbstractJpaTest {

    private static final String PASSWORD = "pa66word!";
    private static final long USER_ID = 1L;
    private static final String PHOTO_FILE_NAME = "image.png";
    private static final int BYTE_ARRAY_LENGTH = 32;
    private static final String EMAIL_TO_UPDATE = "updateemail@test.ru";
    private static final String TEST_2_USER = "test2";

    @Inject
    private CommonConfig commonConfig;
    @Inject
    private UserMapper userMapper;
    @Inject
    private UserEntityRepository userEntityRepository;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private UserPhotoRepository userPhotoRepository;

    private UserService userService;

    @Override
    public void init() {
        roleRepository.save(createRoleEntity());
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        userService = new UserService(commonConfig, passwordEncoder, userMapper, userEntityRepository, roleRepository,
                userPhotoRepository);
    }

    @Override
    public void deleteAll() {
        userPhotoRepository.deleteAll();
        userEntityRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testCreateUser() {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        UserEntity userEntity = userService.createUser(createUserDto, PASSWORD);
        UserEntity actual = userEntityRepository.findById(userEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getLogin()).isEqualTo(createUserDto.getLogin());
        assertThat(actual.getPasswordDate()).isNotNull();
    }

    @Test
    void testCreateUserWithNotExistingRole() {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        assertThrows(IllegalStateException.class, () -> userService.createUser(createUserDto, PASSWORD));
    }

    @Test
    void testUpdateEmail() {
        UserEntity userEntity = createAndSaveUser();
        userService.updateEmail(userEntity.getId(), EMAIL_TO_UPDATE);
        UserEntity actual = userEntityRepository.findById(userEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getEmail()).isEqualTo(EMAIL_TO_UPDATE);
    }

    @Test
    void testEmailDuplicationError() {
        Long firstUserId = createAndSaveUser().getId();
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        createUserDto.setLogin(TEST_2_USER);
        createUserDto.setEmail(EMAIL_TO_UPDATE);
        userService.createUser(createUserDto, PASSWORD);
        assertThrows(EmailDuplicationException.class, () -> userService.updateEmail(firstUserId, EMAIL_TO_UPDATE));
    }

    @Test
    void testGetUsersPage() {
        UserEntity userEntity = createAndSaveUser();
        PageRequestDto pageRequestDto =
                new PageRequestDto(0, 10, CREATION_DATE, true, userEntity.getLogin(), Collections.emptyList());
        Page<UserEntity> usersPage = userService.getNextPage(pageRequestDto);
        assertThat(usersPage).isNotNull();
        assertThat(usersPage.getContent()).hasSize(1);
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
                new PageRequestDto(0, 10, FULL_NAME, true, null, Collections.emptyList());
        Page<UserEntity> usersPage = userService.getNextPage(pageRequestDto);
        assertThat(usersPage).isNotNull();
        assertThat(usersPage.getContent()).hasSize(3);
        Iterator<UserEntity> iterator = usersPage.iterator();
        assertThat(iterator.next().getId()).isEqualTo(third.getId());
        assertThat(iterator.next().getId()).isEqualTo(second.getId());
        assertThat(iterator.next().getId()).isEqualTo(first.getId());
    }

    @Test
    void testSetTfaEnabled() {
        UserEntity userEntity = createAndSaveUser();
        userService.setTfaEnabled(userEntity.getId(), true);
        UserEntity actual = userEntityRepository.findById(userEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.isTfaEnabled()).isTrue();
    }

    @Test
    void testSetTfaEnabledForNotExistingUser() {
        assertThrows(EntityNotFoundException.class, () -> userService.setTfaEnabled(USER_ID, true));
    }

    @Test
    void testUpdatePhotoForNotExistingUser() {
        MultipartFile file = mock(MultipartFile.class);
        assertThrows(EntityNotFoundException.class, () -> userService.updatePhoto(USER_ID, file));
    }

    @Test
    void testUpdatePhoto() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(PHOTO_FILE_NAME);
        when(file.getBytes()).thenReturn(new byte[BYTE_ARRAY_LENGTH]);
        UserEntity userEntity = createAndSaveUser();
        userService.updatePhoto(userEntity.getId(), file);
        UserPhoto userPhoto = userPhotoRepository.findByUserEntity(userEntity);
        assertThat(userPhoto).isNotNull();
        assertThat(userPhoto.getFileName()).isEqualTo(PHOTO_FILE_NAME);
        assertThat(userPhoto.getFileExtension()).isEqualTo(FilenameUtils.getExtension(PHOTO_FILE_NAME));
        assertThat(userPhoto.getPhoto()).isNotNull();
        assertThat(userPhoto.getPhoto()).hasSize(BYTE_ARRAY_LENGTH);
    }

    @Test
    void testDeletePhotoForNotExistingUser() {
        assertThrows(EntityNotFoundException.class, () -> userService.deletePhoto(USER_ID));
    }

    @Test
    void testDeleteNotExistingPhoto() {
        UserEntity userEntity = createAndSaveUser();
        assertThrows(EntityNotFoundException.class, () -> userService.deletePhoto(userEntity.getId()));
    }

    @Test
    void testDeletePhoto() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(PHOTO_FILE_NAME);
        when(file.getBytes()).thenReturn(new byte[BYTE_ARRAY_LENGTH]);
        UserEntity userEntity = createAndSaveUser();
        userService.updatePhoto(userEntity.getId(), file);
        userService.deletePhoto(userEntity.getId());
        assertThat(userPhotoRepository.findByUserEntity(userEntity)).isNull();
    }

    private UserEntity createAndSaveUser() {
        CreateUserDto createUserDto = TestHelperUtils.createUserDto();
        return userService.createUser(createUserDto, PASSWORD);
    }
}
