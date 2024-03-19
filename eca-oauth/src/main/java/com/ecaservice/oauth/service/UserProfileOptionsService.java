package com.ecaservice.oauth.service;

import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.oauth.dto.UpdateUserNotificationOptionsDto;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import com.ecaservice.oauth.mapping.UserProfileOptionsMapper;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import com.ecaservice.web.dto.model.UserProfileNotificationOptionsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.oauth.config.audit.AuditCodes.UPDATE_USER_PROFILE_NOTIFICATION_OPTIONS;

/**
 * UserProfile options service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileOptionsService {

    private final UserProfileOptionsMapper userProfileOptionsMapper;
    private final UserProfileOptionsDataService userProfileOptionsDataService;

    /**
     * Gets user profile options.
     *
     * @param user - user login
     * @return user profile dto
     */
    public UserProfileOptionsDto getUserProfileOptions(String user) {
        log.info("Starting to get user [{}] profile options", user);
        var userProfileOptions = userProfileOptionsDataService.getOrCreateProfileOptions(user);
        var userProfileOptionsDto = userProfileOptionsMapper.mapToDto(userProfileOptions);
        log.info("User [{}] profile options has been fetched: {}", user, userProfileOptionsDto);
        return userProfileOptionsDto;
    }

    /**
     * Gets user profile notification options.
     *
     * @param user - user login
     * @return user profile notification options dto
     */
    public UserProfileNotificationOptionsDto getUserNotificationOptions(String user) {
        log.info("Starting to get user [{}] profile notification options", user);
        var userProfileOptions = userProfileOptionsDataService.getOrCreateProfileOptions(user);
        var userProfileNotificationOptionsDto =
                userProfileOptionsMapper.mapToNotificationOptionsDto(userProfileOptions);
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
    public UserProfileOptionsEntity updateUserNotificationOptions(String user,
                                                                  UpdateUserNotificationOptionsDto updateUserNotificationOptionsDto) {
        return userProfileOptionsDataService.updateUserNotificationOptions(user, updateUserNotificationOptionsDto);
    }
}
