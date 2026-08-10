package com.ecaservice.notification.service;

import com.ecaservice.notification.config.UserNotificationProperties;
import com.ecaservice.notification.entity.NotificationEventOptionsEntity;
import com.ecaservice.notification.entity.NotificationOptionsEntity;
import com.ecaservice.notification.mapping.NotificationOptionsMapper;
import com.ecaservice.notification.repository.NotificationEventOptionsRepository;
import com.ecaservice.notification.repository.NotificationOptionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User notification options configuration service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotificationOptionsConfigurationService {

    private final UserNotificationProperties userNotificationProperties;
    private final NotificationOptionsMapper notificationOptionsMapper;
    private final NotificationOptionsRepository notificationOptionsRepository;
    private final NotificationEventOptionsRepository notificationEventOptionsRepository;

    /**
     * Creates and save user notification options with default settings.
     *
     * @param user - user
     * @return user notification options entity
     */
    @Transactional
    public NotificationOptionsEntity createAndSaveDefaultOptions(String user) {
        log.info("Starting to create and save user [{}] notification default options", user);
        var userProfileOptions = notificationOptionsMapper.map(userNotificationProperties);
        userProfileOptions.setUser(user);
        userProfileOptions.setCreated(LocalDateTime.now());
        var userProfileOptionsEntity = notificationOptionsRepository.save(userProfileOptions);
        var notificationEventOptions = saveUserNotificationEvents(userProfileOptionsEntity);
        userProfileOptionsEntity.setNotificationEventOptions(notificationEventOptions);
        log.info("User [{}] notification default options has been created", user);
        return userProfileOptionsEntity;
    }

    private List<NotificationEventOptionsEntity> saveUserNotificationEvents(
            NotificationOptionsEntity notificationOptionsEntity) {
        List<NotificationEventOptionsEntity> userNotificationEventsList = userNotificationProperties
                .getNotificationEventOptions()
                .stream()
                .map(userNotificationEventProperties -> {
                    var notificationEventOptionsEntity =
                            notificationOptionsMapper.map(userNotificationEventProperties);
                    notificationEventOptionsEntity.setNotificationOptions(notificationOptionsEntity);
                    return notificationEventOptionsEntity;
                })
                .collect(Collectors.toList());
        return notificationEventOptionsRepository.saveAll(userNotificationEventsList);
    }
}
