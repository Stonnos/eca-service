package com.ecaservice.notification.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.notification.dto.UpdateUserNotificationEventOptionsDto;
import com.ecaservice.notification.dto.UpdateUserNotificationOptionsDto;
import com.ecaservice.notification.entity.NotificationEventType;
import com.ecaservice.notification.entity.NotificationOptionsEntity;
import com.ecaservice.notification.exception.DuplicateNotificationEventToUpdateException;
import com.ecaservice.notification.exception.NotificationEventNotFoundException;
import com.ecaservice.notification.repository.NotificationEventOptionsRepository;
import com.ecaservice.notification.repository.NotificationOptionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Service to manage with user notification options data.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationOptionsDataService {

    private final UserNotificationOptionsConfigurationService userNotificationOptionsConfigurationService;
    private final NotificationOptionsRepository notificationOptionsRepository;
    private final NotificationEventOptionsRepository notificationEventOptionsRepository;

    /**
     * Gets or create user notification options.
     *
     * @param user - user login
     * @return user notification options entity
     */
    @Locked(lockName = "getOrCreateProfileOptions", key = "#user")
    public NotificationOptionsEntity getOrCreateOptions(String user) {
        log.info("Starting to get or create user [{}] notification options", user);
        var notificationOptions = notificationOptionsRepository.findByUser(user);
        if (notificationOptions == null) {
            log.info("User [{}] notification options not found. Starting to create user notification default options",
                    user);
            notificationOptions = userNotificationOptionsConfigurationService.createAndSaveDefaultOptions(user);
        }
        log.info("User [{}] notification options has been fetched", user);
        return notificationOptions;
    }

    /**
     * Updates user notification options.
     *
     * @param user                       - user login
     * @param userNotificationOptionsDto - notification options dto for update
     * @return user notification options entity
     */
    @Transactional
    public NotificationOptionsEntity updateUserNotificationOptions(String user,
                                                                   UpdateUserNotificationOptionsDto userNotificationOptionsDto) {
        log.info("Starting to update user [{}] notification options: {}", user, userNotificationOptionsDto);
        var userProfileOptions = notificationOptionsRepository.findByUser(user);
        if (userProfileOptions == null) {
            throw new EntityNotFoundException(NotificationOptionsEntity.class, user);
        }
        userProfileOptions.setEmailEnabled(userNotificationOptionsDto.isEmailEnabled());
        userProfileOptions.setWebPushEnabled(userNotificationOptionsDto.isWebPushEnabled());
        updateNotificationEvents(userProfileOptions, userNotificationOptionsDto);
        notificationOptionsRepository.save(userProfileOptions);
        log.info("User [{}] profile notification options has been updated: {}", user, userNotificationOptionsDto);
        return userProfileOptions;
    }

    private void updateNotificationEvents(NotificationOptionsEntity userProfileOptionsEntity,
                                          UpdateUserNotificationOptionsDto updateUserNotificationOptionsDto) {
        if (!CollectionUtils.isEmpty(updateUserNotificationOptionsDto.getNotificationEventOptions())) {
            Map<NotificationEventType, UpdateUserNotificationEventOptionsDto> eventsToUpdate = newHashMap();
            for (var notificationEventOptionsDto : updateUserNotificationOptionsDto.getNotificationEventOptions()) {
                if (eventsToUpdate.containsKey(notificationEventOptionsDto.getEventType())) {
                    throw new DuplicateNotificationEventToUpdateException(notificationEventOptionsDto.getEventType());
                }
                eventsToUpdate.put(notificationEventOptionsDto.getEventType(), notificationEventOptionsDto);
            }
            var userNotificationEventsList =
                    notificationEventOptionsRepository.findByNotificationOptionsAndEventTypeIn(
                            userProfileOptionsEntity, eventsToUpdate.keySet());
            // Checks not found events in database
            if (userNotificationEventsList.size() != eventsToUpdate.size()) {
                var notFoundEvents = eventsToUpdate.keySet()
                        .stream()
                        .filter(eventType -> userNotificationEventsList.stream()
                                .noneMatch(
                                        userNotificationEventOptionsEntity -> eventType.equals(
                                                userNotificationEventOptionsEntity.getEventType()))
                        ).collect(Collectors.toList());
                throw new NotificationEventNotFoundException(notFoundEvents);
            }
            for (var notificationEventEntity : userNotificationEventsList) {
                var notificationEventOptionsDto = eventsToUpdate.get(notificationEventEntity.getEventType());
                notificationEventEntity.setEmailEnabled(notificationEventOptionsDto.isEmailEnabled());
                notificationEventEntity.setWebPushEnabled(notificationEventOptionsDto.isWebPushEnabled());
            }
            notificationEventOptionsRepository.saveAll(userNotificationEventsList);
        }
    }
}
