package com.ecaservice.server.service;

import com.ecaservice.server.bpm.model.UserProfileOptionsModel;
import com.ecaservice.server.bpm.model.UserProfileOptionsNotificationEventModel;
import com.ecaservice.user.profile.options.client.service.UserProfileOptionsProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * User profile options service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileOptionsService {

    private final UserProfileOptionsProvider userProfileOptionsProvider;

    /**
     * Gets user profile options model by login.
     *
     * @param user - user login
     * @return user profile options model
     */
    public UserProfileOptionsModel getUserProfileOptionsModel(String user) {
        log.info("Starting to get user [{}] profile options model", user);
        UserProfileOptionsModel userProfileOptionsModel = new UserProfileOptionsModel();
        try {
            var userProfileOptions = userProfileOptionsProvider.getUserProfileOptions(user);
            userProfileOptionsModel.setEmailEnabled(userProfileOptions.isEmailEnabled());
            userProfileOptionsModel.setWebPushEnabled(userProfileOptions.isWebPushEnabled());
            var notificationEventOptions = userProfileOptions.getNotificationEventOptions()
                    .stream()
                    .collect(Collectors.toMap(eventOptionsDto -> eventOptionsDto.getEventType().name(),
                            eventOptionsDto -> new UserProfileOptionsNotificationEventModel(
                                    eventOptionsDto.isEmailEnabled(), eventOptionsDto.isWebPushEnabled()))
                    );
            userProfileOptionsModel.setNotificationEventOptions(notificationEventOptions);
        } catch (Exception ex) {
            log.error(
                    "Error while get user [{}] profile options. Default options model will be used. Error details: {}",
                    user, ex.getMessage());
        }
        log.info("User [{}] profile options model has been fetched: {}", user, userProfileOptionsModel);
        return userProfileOptionsModel;
    }
}
