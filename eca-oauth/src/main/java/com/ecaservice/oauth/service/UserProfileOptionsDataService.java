package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.oauth.config.UserProfileProperties;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import com.ecaservice.oauth.mapping.UserProfileOptionsMapper;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.repository.UserProfileOptionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service to manage with user profile options data.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileOptionsDataService {

    private final UserProfileProperties userProfileProperties;
    private final UserProfileOptionsMapper userProfileOptionsMapper;
    private final UserEntityRepository userEntityRepository;
    private final UserProfileOptionsRepository userProfileOptionsRepository;

    /**
     * Gets or create user profile options.
     *
     * @param user - user login
     * @return user profile entity
     */
    @Locked(lockName = "getOrCreateProfileOptions", key = "#user")
    public UserProfileOptionsEntity getOrCreateProfileOptions(String user) {
        log.info("Starting to get or create user [{}] profile options", user);
        var userEntity = userEntityRepository.findByLogin(user)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, user));
        var userProfileOptions = userProfileOptionsRepository.findByUserEntity(userEntity);
        if (userProfileOptions == null) {
            log.info("User [{}] profile options not found. Starting to create and save user profile default options",
                    user);
            userProfileOptions = createAndSaveNewProfileOptions(userEntity);
            log.info("User [{}] profile default options has been created", user);
        }
        log.info("User [{}] profile options has been fetched", user);
        return userProfileOptions;
    }

    private UserProfileOptionsEntity createAndSaveNewProfileOptions(UserEntity userEntity) {
        var userProfileOptions = userProfileOptionsMapper.map(userProfileProperties);
        userProfileOptions.setUserEntity(userEntity);
        userProfileOptions.setCreated(LocalDateTime.now());
        return userProfileOptionsRepository.save(userProfileOptions);
    }
}
