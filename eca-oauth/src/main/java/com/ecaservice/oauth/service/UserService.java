package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.common.web.exception.FileProcessingException;
import com.ecaservice.common.web.exception.InvalidFileException;
import com.ecaservice.common.web.exception.InvalidOperationException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.dto.UpdateUserInfoDto;
import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserPhoto;
import com.ecaservice.oauth.exception.UserLockNotAllowedException;
import com.ecaservice.oauth.exception.UserLockedException;
import com.ecaservice.oauth.filter.UserFilter;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.oauth.projection.UserPhotoIdProjection;
import com.ecaservice.oauth.repository.RoleRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.repository.UserPhotoRepository;
import com.ecaservice.user.dto.UserInfoDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UserDictionaryDto;
import com.ecaservice.web.dto.model.UserDto;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.common.web.util.FileUtils.isValidExtension;
import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.oauth.config.audit.AuditCodes.CREATE_USER;
import static com.ecaservice.oauth.config.audit.AuditCodes.DELETE_PHOTO;
import static com.ecaservice.oauth.config.audit.AuditCodes.DISABLE_2FA;
import static com.ecaservice.oauth.config.audit.AuditCodes.ENABLE_2FA;
import static com.ecaservice.oauth.config.audit.AuditCodes.LOCK_USER;
import static com.ecaservice.oauth.config.audit.AuditCodes.UNLOCK_USER;
import static com.ecaservice.oauth.config.audit.AuditCodes.UPDATE_PERSONAL_DATA;
import static com.ecaservice.oauth.config.audit.AuditCodes.UPDATE_PHOTO;
import static com.ecaservice.oauth.dictionary.FilterDictionaries.USERS_TEMPLATE;
import static com.ecaservice.oauth.entity.UserEntity_.CREATION_DATE;
import static com.ecaservice.oauth.util.Utils.isSuperAdmin;
import static com.ecaservice.user.model.Role.ROLE_ECA_USER;

/**
 * Users management service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class UserService {

    private final AppProperties appProperties;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final Oauth2RevokeTokenService oauth2RevokeTokenService;
    private final UserProfileOptionsConfigurationService userProfileOptionsConfigurationService;
    private final UserProfileOptionsDataEventService userProfileOptionsDataEventService;
    private final FilterTemplateService filterTemplateService;
    private final UserEntityRepository userEntityRepository;
    private final RoleRepository roleRepository;
    private final UserPhotoRepository userPhotoRepository;

    /**
     * Gets users next page for specified page request.
     *
     * @param pageRequestDto - page request
     * @return users entities page
     */
    public Page<UserEntity> getNextPage(PageRequestDto pageRequestDto) {
        log.info("Gets users next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortFields(), CREATION_DATE, true);
        var globalFilterFields = filterTemplateService.getGlobalFilterFields(USERS_TEMPLATE);
        UserFilter filter =
                new UserFilter(pageRequestDto.getSearchQuery(), globalFilterFields, pageRequestDto.getFilters());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var usersPage = userEntityRepository.findAll(filter, pageRequest);
        log.info("Users page [{} of {}] with size [{}] has been fetched for page request [{}]", usersPage.getNumber(),
                usersPage.getTotalPages(), usersPage.getNumberOfElements(), pageRequestDto);
        return usersPage;
    }

    /**
     * Gets users next page for specified page request.
     *
     * @param pageRequestDto - page request
     * @return users dto page
     */
    public PageDto<UserDto> getUsersPage(
            @ValidPageRequest(filterTemplateName = USERS_TEMPLATE) PageRequestDto pageRequestDto) {
        var usersPage = getNextPage(pageRequestDto);
        var userDtoList = userMapper.map(usersPage.getContent());
        populateUsersPhotoIds(usersPage.getContent(), userDtoList);
        return PageDto.of(userDtoList, pageRequestDto.getPage(), usersPage.getTotalElements());
    }

    /**
     * Gets users dictionary next page for specified page request.
     *
     * @param pageRequestDto - page request
     * @return users dictionary dto page
     */
    public PageDto<UserDictionaryDto> getUsersDictionaryPage(
            @ValidPageRequest(filterTemplateName = USERS_TEMPLATE) PageRequestDto pageRequestDto) {
        var usersPage = getNextPage(pageRequestDto);
        var userDictionaryDtoList = userMapper.mapToDictionaryList(usersPage.getContent());
        return PageDto.of(userDictionaryDtoList, pageRequestDto.getPage(), usersPage.getTotalElements());
    }

    /**
     * Creates user.
     *
     * @param createUserDto - create user dto
     * @param password      - user password
     * @return user entity
     */
    @Audit(CREATE_USER)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserEntity createUser(CreateUserDto createUserDto, String password) {
        log.info("Starting to create user [{}] has been created", createUserDto.getLogin());
        UserEntity userEntity = userMapper.map(createUserDto);
        userEntity.setPassword(passwordEncoder.encode(password));
        populateUserRole(userEntity);
        userEntity.setForceChangePassword(true);
        userEntity.setCreationDate(LocalDateTime.now());
        userEntityRepository.save(userEntity);
        //Also creates user profile options with default settings
        var userProfileOptionsEntity =
                userProfileOptionsConfigurationService.createAndSaveDefaultProfileOptions(userEntity);
        userProfileOptionsDataEventService.saveEvent(userProfileOptionsEntity);
        log.info("User {} has been created", userEntity.getId());
        return userEntity;
    }

    /**
     * Updates user info.
     *
     * @param user              - username
     * @param updateUserInfoDto - user info dto
     */
    @Audit(UPDATE_PERSONAL_DATA)
    public void updateUserInfo(String user, UpdateUserInfoDto updateUserInfoDto) {
        log.info("Starting to update user [{}] info", user);
        UserEntity userEntity = getByLogin(user);
        userMapper.update(updateUserInfoDto, userEntity);
        userEntityRepository.save(userEntity);
        log.info("User [{}] info has been updated", user);
    }

    /**
     * Gets user entity by id.
     *
     * @param id - user id
     * @return - user entity
     */
    public UserEntity getById(long id) {
        log.debug("Gets user by id [{}]", id);
        return userEntityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, id));
    }

    /**
     * Gets user details by id.
     *
     * @param login - user login
     * @return - user dto
     */
    public UserDto getUserDetails(String login) {
        log.info("Gets user [{}] info", login);
        var userEntity = getByLogin(login);
        UserDto userDto = userMapper.map(userEntity);
        userDto.setPhotoId(userPhotoRepository.getUserPhotoId(userEntity));
        log.info("User [{}] info has been fetched", login);
        return userDto;
    }

    /**
     * Gets user info dto by login.
     *
     * @param login - user login
     * @return user info dto
     */
    public UserInfoDto getUserInfo(String login) {
        log.info("Gets user info by login [{}]", login);
        var user = getByLogin(login);
        return userMapper.mapToUserInfo(user);
    }

    /**
     * Enable two factor authentication for user.
     *
     * @param user - username
     */
    @Audit(ENABLE_2FA)
    public void enableTfa(String user) {
        log.info("Starting to enable tfa for user [{}]", user);
        UserEntity userEntity = getByLogin(user);
        if (userEntity.isTfaEnabled()) {
            throw new InvalidOperationException("Tfa is already enabled for user");
        }
        userEntity.setTfaEnabled(true);
        userEntityRepository.save(userEntity);
        log.info("Tfa has been enabled for user [{}]", userEntity.getId());
    }

    /**
     * Disable two factor authentication for user.
     *
     * @param user - username
     */
    @Audit(DISABLE_2FA)
    public void disableTfa(String user) {
        log.info("Starting to disable tfa for user [{}]", user);
        UserEntity userEntity = getByLogin(user);
        if (!userEntity.isTfaEnabled()) {
            throw new InvalidOperationException("Tfa is already disabled");
        }
        userEntity.setTfaEnabled(false);
        userEntityRepository.save(userEntity);
        log.info("Tfa has been disabled for user [{}]", userEntity.getId());
    }

    /**
     * Locks user account.
     *
     * @param userId - user id
     * @return locked user
     */
    @Audit(LOCK_USER)
    @Transactional
    public UserEntity lock(long userId) {
        log.info("Starting to lock user [{}]", userId);
        UserEntity userEntity = getById(userId);
        if (isSuperAdmin(userEntity)) {
            throw new UserLockNotAllowedException();
        }
        if (userEntity.isLocked()) {
            throw new UserLockedException(userId);
        }
        userEntity.setLocked(true);
        userEntityRepository.save(userEntity);
        oauth2RevokeTokenService.revokeTokens(userEntity);
        log.info("User [{}] has been locked", userId);
        return userEntity;
    }

    /**
     * Unlocks user account.
     *
     * @param userId - user id
     * @return unlocked user
     */
    @Audit(UNLOCK_USER)
    public UserEntity unlock(long userId) {
        log.info("Starting to unlock user [{}]", userId);
        UserEntity userEntity = getById(userId);
        if (!userEntity.isLocked()) {
            throw new InvalidOperationException("User is already unlocked");
        }
        userEntity.setLocked(false);
        userEntityRepository.save(userEntity);
        log.info("User [{}] has been unlocked", userId);
        return userEntity;
    }

    /**
     * Updates photo for specified user.
     *
     * @param user - username
     * @param file - user photo file
     */
    @Audit(UPDATE_PHOTO)
    public void updatePhoto(String user, MultipartFile file) {
        log.info("Starting to update user [{}] photo: [{}]", user, file.getOriginalFilename());
        UserEntity userEntity = getByLogin(user);
        if (!isValidExtension(file.getOriginalFilename(), appProperties.getValidUserPhotoFileExtensions())) {
            throw new InvalidFileException(
                    String.format("Invalid file [%s] extension. Expected one of %s", file.getOriginalFilename(),
                            appProperties.getValidUserPhotoFileExtensions()));
        }
        UserPhoto userPhoto = userPhotoRepository.findByUserEntity(userEntity);
        if (userPhoto == null) {
            userPhoto = new UserPhoto();
            userPhoto.setUserEntity(userEntity);
        }
        updatePhoto(userPhoto, file);
        log.info("New photo [{}] has been updated for user [{}]", userPhoto.getId(), user);
    }

    /**
     * Deletes photo for specified user.
     *
     * @param user - username
     */
    @Audit(DELETE_PHOTO)
    @Transactional
    public void deletePhoto(String user) {
        log.info("Starting to delete user [{}] photo", user);
        UserEntity userEntity = getByLogin(user);
        Long userPhotoId = userPhotoRepository.getUserPhotoId(userEntity);
        if (userPhotoId == null) {
            throw new EntityNotFoundException(UserPhoto.class, String.format("User %d", userEntity.getId()));
        }
        userPhotoRepository.deleteById(userPhotoId);
        log.info("User [{}] photo has been deleted", user);
    }

    private void populateUserRole(UserEntity userEntity) {
        RoleEntity roleEntity = roleRepository.findByRoleName(ROLE_ECA_USER)
                .orElseThrow(() -> new EntityNotFoundException(RoleEntity.class, ROLE_ECA_USER));
        userEntity.setRoles(Sets.newHashSet(roleEntity));
    }

    private void updatePhoto(UserPhoto userPhoto, MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            userPhoto.setFileName(fileName);
            userPhoto.setFileExtension(FilenameUtils.getExtension(fileName));
            userPhoto.setPhoto(file.getBytes());
            userPhotoRepository.save(userPhoto);
        } catch (IOException ex) {
            throw new FileProcessingException(ex.getMessage());
        }
    }

    private void populateUsersPhotoIds(List<UserEntity> userEntities, List<UserDto> userDtoList) {
        var userPhotoIdsMap = userPhotoRepository.getUserPhotoIds(userEntities)
                .stream()
                .collect(Collectors.toMap(UserPhotoIdProjection::getUserId, UserPhotoIdProjection::getId));
        userDtoList.forEach(userDto -> userDto.setPhotoId(userPhotoIdsMap.get(userDto.getId())));
    }

    public UserEntity getByLogin(String login) {
        return userEntityRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, login));
    }
}
