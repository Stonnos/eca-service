package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.CommonConfig;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserPhoto;
import com.ecaservice.oauth.exception.EntityNotFoundException;
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
        populateUserRole(userEntity);
        userEntity.setCreationDate(LocalDateTime.now());
        return userEntityRepository.save(userEntity);
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
     * Enable/Disable two factor authentication for user.
     *
     * @param userId     - user id
     * @param tfaEnabled - tfa enabled?
     */
    public void setTfaEnabled(long userId, boolean tfaEnabled) {
        UserEntity userEntity = getById(userId);
        userEntity.setTfaEnabled(tfaEnabled);
        userEntityRepository.save(userEntity);
        log.info("Sets two factor authentication flag [{}] for user [{}]", tfaEnabled, userEntity.getLogin());
    }

    /**
     * Updates photo for specified user.
     *
     * @param userId - user id
     * @param file   - user photo file
     */
    public void updatePhoto(long userId, MultipartFile file) {
        UserEntity userEntity = getById(userId);
        UserPhoto userPhoto = userPhotoRepository.findByUserEntity(userEntity);
        if (userPhoto == null) {
            userPhoto = new UserPhoto();
            userPhoto.setUserEntity(userEntity);
        }
        updatePhoto(userPhoto, file);
    }

    /**
     * Deletes photo for specified user.
     *
     * @param userId - user id
     */
    @Transactional
    public void deletePhoto(long userId) {
        UserEntity userEntity = getById(userId);
        UserPhoto userPhoto = userPhotoRepository.findByUserEntity(userEntity);
        if (userPhoto == null) {
            throw new EntityNotFoundException(UserPhoto.class, String.format("User %s", userEntity.getLogin()));
        }
        userPhotoRepository.delete(userPhoto);
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
