package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.config.CommonConfig;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.dto.UpdateUserInfoDto;
import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserPhoto;
import com.ecaservice.oauth.exception.EmailDuplicationException;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.ecaservice.oauth.entity.UserEntity_.CREATION_DATE;
import static com.ecaservice.oauth.util.FilterUtils.buildSort;
import static com.ecaservice.oauth.util.FilterUtils.buildSpecification;
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

    private final CommonConfig commonConfig;
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
        Specification<UserEntity> specification = buildSpecification(pageRequestDto);
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return userEntityRepository.findAll(specification, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }

    /**
     * Creates user.
     *
     * @param createUserDto - create user dto
     * @param password      - user password
     * @return user entity
     */
    public UserEntity createUser(CreateUserDto createUserDto, String password) {
        UserEntity userEntity = userMapper.map(createUserDto);
        userEntity.setPassword(passwordEncoder.encode(password));
        userEntity.setPasswordDate(LocalDateTime.now());
        populateUserRole(userEntity);
        userEntity.setCreationDate(LocalDateTime.now());
        return userEntityRepository.save(userEntity);
    }

    /**
     * Updates user info.
     *
     * @param userId            - user id
     * @param updateUserInfoDto - user info dto
     */
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
        return userEntityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(UserEntity.class, id));
    }

    /**
     * Updates email for user.
     *
     * @param userId   - user id
     * @param newEmail - new email
     */
    public void updateEmail(long userId, String newEmail) {
        log.info("Starting to update email for user [{}]", userId);
        String emailToUpdate = newEmail.trim();
        UserEntity userEntity = getById(userId);
        if (!userEntity.getEmail().equals(emailToUpdate)) {
            if (userEntityRepository.existsByEmail(emailToUpdate)) {
                throw new EmailDuplicationException(userId);
            }
            userEntity.setEmail(emailToUpdate);
            userEntityRepository.save(userEntity);
            log.info("Email has been updated for user [{}]", userId);
        }
    }

    /**
     * Enable/Disable two factor authentication for user.
     *
     * @param userId     - user id
     * @param tfaEnabled - tfa enabled?
     */
    public void setTfaEnabled(long userId, boolean tfaEnabled) {
        log.info("Starting to set tfa flag [{}] for user [{}]", tfaEnabled, userId);
        UserEntity userEntity = getById(userId);
        userEntity.setTfaEnabled(tfaEnabled);
        userEntityRepository.save(userEntity);
        log.info("Tfa flag [{}] has been set for user [{}]", tfaEnabled, userEntity.getId());
    }

    /**
     * Locks user account.
     *
     * @param userId - user id
     */
    @Transactional
    public void lock(long userId) {
        log.info("Starting to lock user [{}]", userId);
        UserEntity userEntity = getById(userId);
        if (userEntity.isLocked()) {
            throw new IllegalStateException(String.format("User [%d] was locked", userId));
        }
        userEntity.setLocked(true);
        userEntityRepository.save(userEntity);
        oauth2TokenService.revokeTokens(userEntity);
        log.info("User [{}] has been locked", userId);
    }

    /**
     * Unlocks user account.
     *
     * @param userId - user id
     */
    public void unlock(long userId) {
        log.info("Starting to unlock user [{}]", userId);
        UserEntity userEntity = getById(userId);
        if (!userEntity.isLocked()) {
            throw new IllegalStateException(String.format("User [%d] was unlocked", userId));
        }
        userEntity.setLocked(false);
        userEntityRepository.save(userEntity);
        log.info("User [{}] has been unlocked", userId);
    }

    /**
     * Updates photo for specified user.
     *
     * @param userId - user id
     * @param file   - user photo file
     */
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
        RoleEntity roleEntity = roleRepository.findByRoleName(ROLE_ECA_USER).orElseThrow(
                () -> new IllegalStateException(String.format("Role with name [%s] doesn't exists", ROLE_ECA_USER)));
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
