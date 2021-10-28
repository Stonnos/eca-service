package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.dto.UpdateUserInfoDto;
import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserEntity_;
import com.ecaservice.oauth.entity.UserPhoto;
import com.ecaservice.oauth.filter.UserFilter;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.oauth.repository.RoleRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.repository.UserPhotoRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.oauth.config.audit.AuditCodes.CREATE_USER;
import static com.ecaservice.oauth.config.audit.AuditCodes.DELETE_PHOTO;
import static com.ecaservice.oauth.config.audit.AuditCodes.DISABLE_2FA;
import static com.ecaservice.oauth.config.audit.AuditCodes.ENABLE_2FA;
import static com.ecaservice.oauth.config.audit.AuditCodes.LOCK_USER;
import static com.ecaservice.oauth.config.audit.AuditCodes.UNLOCK_USER;
import static com.ecaservice.oauth.config.audit.AuditCodes.UPDATE_PERSONAL_DATA;
import static com.ecaservice.oauth.config.audit.AuditCodes.UPDATE_PHOTO;
import static com.ecaservice.oauth.entity.UserEntity_.CREATION_DATE;
import static com.ecaservice.oauth.util.Utils.isSuperAdmin;
import static com.ecaservice.user.model.Role.ROLE_ECA_USER;

/**
 * Users management service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final List<String> USER_GLOBAL_FILTER_FIELDS = List.of(
            UserEntity_.LOGIN,
            UserEntity_.EMAIL,
            UserEntity_.FULL_NAME
    );

    private final AppProperties appProperties;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final Oauth2TokenService oauth2TokenService;
    private final UserEntityRepository userEntityRepository;
    private final RoleRepository roleRepository;
    private final UserPhotoRepository userPhotoRepository;

    /**
     * Gets the next page for specified page request.
     *
     * @param pageRequestDto - page request
     * @return entities page
     */
    public Page<UserEntity> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        UserFilter filter =
                new UserFilter(pageRequestDto.getSearchQuery(), USER_GLOBAL_FILTER_FIELDS, pageRequestDto.getFilters());
        int pageSize = Integer.min(pageRequestDto.getSize(), appProperties.getMaxPageSize());
        return userEntityRepository.findAll(filter, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }

    /**
     * Creates user.
     *
     * @param createUserDto - create user dto
     * @param password      - user password
     * @return user entity
     */
    @Audit(CREATE_USER)
    public UserEntity createUser(CreateUserDto createUserDto, String password) {
        UserEntity userEntity = userMapper.map(createUserDto);
        userEntity.setPassword(passwordEncoder.encode(password));
        userEntity.setPasswordDate(LocalDateTime.now());
        populateUserRole(userEntity);
        userEntity.setForceChangePassword(true);
        userEntity.setCreationDate(LocalDateTime.now());
        return userEntityRepository.save(userEntity);
    }

    /**
     * Updates user info.
     *
     * @param userId            - user id
     * @param updateUserInfoDto - user info dto
     */
    @Audit(UPDATE_PERSONAL_DATA)
    public void updateUserInfo(long userId, UpdateUserInfoDto updateUserInfoDto) {
        log.info("Starting to update user [{}] info", userId);
        UserEntity userEntity = getById(userId);
        userMapper.update(updateUserInfoDto, userEntity);
        userEntityRepository.save(userEntity);
        log.info("User [{}] info has been updated", userId);
    }

    /**
     * Gets user entity by id.
     *
     * @param id - user id
     * @return - user entity
     */
    public UserEntity getById(long id) {
        return userEntityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, id));
    }

    /**
     * Enable two factor authentication for user.
     *
     * @param userId - user id
     */
    @Audit(ENABLE_2FA)
    public void enableTfa(long userId) {
        log.info("Starting to enable tfa for user [{}]", userId);
        UserEntity userEntity = getById(userId);
        if (userEntity.isTfaEnabled()) {
            throw new IllegalStateException(String.format("Tfa is already enabled for user [%d]", userId));
        }
        userEntity.setTfaEnabled(true);
        userEntityRepository.save(userEntity);
        log.info("Tfa has been enabled for user [{}]", userEntity.getId());
    }

    /**
     * Disable two factor authentication for user.
     *
     * @param userId - user id
     */
    @Audit(DISABLE_2FA)
    public void disableTfa(long userId) {
        log.info("Starting to disable tfa for user [{}]", userId);
        UserEntity userEntity = getById(userId);
        if (!userEntity.isTfaEnabled()) {
            throw new IllegalStateException(String.format("Tfa is already disabled for user [%d]", userId));
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
            throw new IllegalStateException(String.format("Can't lock super admin user [%d]", userId));
        }
        if (userEntity.isLocked()) {
            throw new IllegalStateException(String.format("User [%d] is already locked", userId));
        }
        userEntity.setLocked(true);
        userEntityRepository.save(userEntity);
        oauth2TokenService.revokeTokens(userEntity);
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
            throw new IllegalStateException(String.format("User [%d] is already unlocked", userId));
        }
        userEntity.setLocked(false);
        userEntityRepository.save(userEntity);
        log.info("User [{}] has been unlocked", userId);
        return userEntity;
    }

    /**
     * Updates photo for specified user.
     *
     * @param userId - user id
     * @param file   - user photo file
     */
    @Audit(UPDATE_PHOTO)
    public void updatePhoto(long userId, MultipartFile file) {
        log.info("Starting to update user [{}] photo: [{}]", userId, file.getOriginalFilename());
        UserEntity userEntity = getById(userId);
        UserPhoto userPhoto = userPhotoRepository.findByUserEntity(userEntity);
        if (userPhoto == null) {
            userPhoto = new UserPhoto();
            userPhoto.setUserEntity(userEntity);
        }
        updatePhoto(userPhoto, file);
        log.info("New photo [{}] has been updated for user [{}]", userPhoto.getId(), userId);
    }

    /**
     * Deletes photo for specified user.
     *
     * @param userId - user id
     */
    @Audit(DELETE_PHOTO)
    @Transactional
    public void deletePhoto(long userId) {
        log.info("Starting to delete user [{}] photo", userId);
        UserEntity userEntity = getById(userId);
        UserPhoto userPhoto = userPhotoRepository.findByUserEntity(userEntity);
        if (userPhoto == null) {
            throw new EntityNotFoundException(UserPhoto.class, String.format("User %d", userEntity.getId()));
        }
        userPhotoRepository.delete(userPhoto);
        log.info("User [{}] photo has been deleted", userId);
    }

    private void populateUserRole(UserEntity userEntity) {
        RoleEntity roleEntity = roleRepository.findByRoleName(ROLE_ECA_USER)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Role with name [%s] doesn't exists", ROLE_ECA_USER)));
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
            throw new IllegalStateException(ex);
        }
    }
}
