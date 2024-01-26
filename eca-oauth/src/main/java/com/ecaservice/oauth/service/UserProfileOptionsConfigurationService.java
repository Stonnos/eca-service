package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.UserProfileProperties;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserNotificationEventOptionsEntity;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import com.ecaservice.oauth.mapping.UserProfileOptionsMapper;
import com.ecaservice.oauth.repository.UserNotificationEventOptionsRepository;
import com.ecaservice.oauth.repository.UserProfileOptionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User profile options configuration service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileOptionsConfigurationService {

    private final UserProfileProperties userProfileProperties;
    private final UserProfileOptionsMapper userProfileOptionsMapper;
    private final UserProfileOptionsRepository userProfileOptionsRepository;
    private final UserNotificationEventOptionsRepository userNotificationEventOptionsRepository;

    /**
     * Creates and save user profile options with default settings.
     *
     * @param userEntity - user entity
     * @return user profile options entity
     */
    @Transactional
    public UserProfileOptionsEntity createAndSaveDefaultProfileOptions(UserEntity userEntity) {
        log.info("Starting to create and save user [{}] profile default options", userEntity.getLogin());
        var userProfileOptions = userProfileOptionsMapper.map(userProfileProperties);
        userProfileOptions.setUserEntity(userEntity);
        userProfileOptions.setCreated(LocalDateTime.now());
        var userProfileOptionsEntity = userProfileOptionsRepository.save(userProfileOptions);
        var notificationEventOptions = saveUserNotificationEvents(userProfileOptionsEntity);
        userProfileOptionsEntity.setNotificationEventOptions(notificationEventOptions);
        log.info("User [{}] profile default options has been created", userEntity.getLogin());
        return userProfileOptionsEntity;
    }

    private List<UserNotificationEventOptionsEntity> saveUserNotificationEvents(
            UserProfileOptionsEntity userProfileOptionsEntity) {
        var userNotificationEventsList = userProfileProperties.getNotificationEventOptions()
                .stream()
                .map(userNotificationEventProperties -> {
                    var notificationEventOptionsEntity = userProfileOptionsMapper.map(userNotificationEventProperties);
                    notificationEventOptionsEntity.setUserProfileOptions(userProfileOptionsEntity);
                    return notificationEventOptionsEntity;
                })
                .collect(Collectors.toList());
        return userNotificationEventOptionsRepository.saveAll(userNotificationEventsList);
    }
}
