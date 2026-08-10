package com.ecaservice.notification.service;

import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.notification.dto.UpdateUserNotificationOptionsDto;
import com.ecaservice.notification.entity.NotificationOptionsEntity;
import com.ecaservice.notification.mapping.NotificationOptionsMapper;
import com.ecaservice.web.dto.model.UserProfileNotificationOptionsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.notification.config.audit.AuditCodes.UPDATE_USER_PROFILE_NOTIFICATION_OPTIONS;

/**
 * UserProfile options service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationOptionsService {

    private final NotificationOptionsMapper notificationOptionsMapper;
    private final NotificationOptionsDataService notificationOptionsDataService;

    /**
     * Gets user profile notification options.
     *
     * @param user - user login
     * @return user profile notification options dto
     */
    public UserProfileNotificationOptionsDto getUserNotificationOptions(String user) {
        log.info("Starting to get user [{}] profile notification options", user);
        var userProfileOptions = notificationOptionsDataService.getOrCreateOptions(user);
        var userProfileNotificationOptionsDto =
                notificationOptionsMapper.mapToNotificationOptionsDto(userProfileOptions);
        log.info("User [{}] profile notification options has been fetched: {}", user,
                userProfileNotificationOptionsDto);
        return userProfileNotificationOptionsDto;
    }

    /**
     * Updates user profile notification options.
     *
     * @param user                             - user login
     * @param updateUserNotificationOptionsDto - notification options dto for update
     * @return user profile options entity
     */
    @Audit(value = UPDATE_USER_PROFILE_NOTIFICATION_OPTIONS)
    public NotificationOptionsEntity updateUserNotificationOptions(String user,
                                                                   UpdateUserNotificationOptionsDto updateUserNotificationOptionsDto) {
        return notificationOptionsDataService.updateUserNotificationOptions(user, updateUserNotificationOptionsDto);
    }
}
