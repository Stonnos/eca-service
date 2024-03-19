package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.oauth.dto.UpdateUserNotificationEventOptionsDto;
import com.ecaservice.oauth.dto.UpdateUserNotificationOptionsDto;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import com.ecaservice.oauth.exception.DuplicateNotificationEventToUpdateException;
import com.ecaservice.oauth.exception.NotificationEventNotFoundException;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.repository.UserNotificationEventOptionsRepository;
import com.ecaservice.oauth.repository.UserProfileOptionsRepository;
import com.ecaservice.user.profile.options.dto.UserNotificationEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Service to manage with user profile options data.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileOptionsDataService {

    private final UserProfileOptionsConfigurationService userProfileOptionsConfigurationService;
    private final UserEntityRepository userEntityRepository;
    private final UserProfileOptionsRepository userProfileOptionsRepository;
    private final UserNotificationEventOptionsRepository userNotificationEventOptionsRepository;

    /**
     * Gets or create user profile options.
     *
     * @param user - user login
     * @return user profile entity
     */
    @Locked(lockName = "getOrCreateProfileOptions", key = "#user")
    public UserProfileOptionsEntity getOrCreateProfileOptions(String user) {
        log.info("Starting to get or create user [{}] profile options", user);
        var userEntity = getUser(user);
        var userProfileOptions = userProfileOptionsRepository.findByUserEntity(userEntity);
        if (userProfileOptions == null) {
            log.info("User [{}] profile options not found. Starting to create and save user profile default options",
                    user);
            userProfileOptions = userProfileOptionsConfigurationService.createAndSaveDefaultProfileOptions(userEntity);
        }
        log.info("User [{}] profile options has been fetched", user);
        return userProfileOptions;
    }

    /**
     * Updates user profile notification options.
     *
     * @param user                             - user login
     * @param updateUserNotificationOptionsDto - notification options dto for update
     * @return user profile options entity
     */
    @Transactional
    public UserProfileOptionsEntity updateUserNotificationOptions(String user,
                                                                  UpdateUserNotificationOptionsDto updateUserNotificationOptionsDto) {
        log.info("Starting to update user [{}] profile notification options: {}", user,
                updateUserNotificationOptionsDto);
        var userEntity = getUser(user);
        var userProfileOptions = userProfileOptionsRepository.findByUserEntity(userEntity);
        if (userProfileOptions == null) {
            throw new EntityNotFoundException(UserProfileOptionsEntity.class, user);
        }
        int version = userProfileOptions.getVersion();
        userProfileOptions.setVersion(++version);
        userProfileOptions.setEmailEnabled(updateUserNotificationOptionsDto.isEmailEnabled());
        userProfileOptions.setWebPushEnabled(updateUserNotificationOptionsDto.isWebPushEnabled());
        updateNotificationEvents(userProfileOptions, updateUserNotificationOptionsDto);
        userProfileOptionsRepository.save(userProfileOptions);
        log.info("User [{}] profile notification options has been updated: {}", user,
                updateUserNotificationOptionsDto);
        return userProfileOptions;
    }

    private void updateNotificationEvents(UserProfileOptionsEntity userProfileOptionsEntity,
                                          UpdateUserNotificationOptionsDto updateUserNotificationOptionsDto) {
        if (!CollectionUtils.isEmpty(updateUserNotificationOptionsDto.getNotificationEventOptions())) {
            Map<UserNotificationEventType, UpdateUserNotificationEventOptionsDto> eventsToUpdate = newHashMap();
            for (var notificationEventOptionsDto : updateUserNotificationOptionsDto.getNotificationEventOptions()) {
                if (eventsToUpdate.containsKey(notificationEventOptionsDto.getEventType())) {
                    throw new DuplicateNotificationEventToUpdateException(notificationEventOptionsDto.getEventType());
                }
                eventsToUpdate.put(notificationEventOptionsDto.getEventType(), notificationEventOptionsDto);
            }
            var userNotificationEventsList =
                    userNotificationEventOptionsRepository.findByUserProfileOptionsAndEventTypeIn(
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
            userNotificationEventOptionsRepository.saveAll(userNotificationEventsList);
        }
    }

    private UserEntity getUser(String user) {
        return userEntityRepository.findByLogin(user)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, user));
    }
}
