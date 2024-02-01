package com.ecaservice.user.profile.options.cache.client.mq.listener;

import com.ecaservice.user.profile.options.cache.client.service.UserProfileOptionsDataService;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.ecaservice.user.profile.options.cache.client.config.rabbit.UserProfileOptionsCacheClientRabbitConfiguration.USER_PROFILE_OPTIONS_CACHE_CLIENT_RABBIT_LISTENER_CONTAINER_FACTORY;

/**
 * User profile options data event message listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserProfileOptionsDataEventMessageListener {

    private final UserProfileOptionsDataService userProfileOptionsDataService;

    /**
     * Handles user profile data updated message from mq.
     *
     * @param userProfileOptionsDto - user profile options dto
     */
    @RabbitListener(containerFactory = USER_PROFILE_OPTIONS_CACHE_CLIENT_RABBIT_LISTENER_CONTAINER_FACTORY,
            queues = "${user-profile.client.cache.rabbit.dataEventQueue}")
    public void handleDataEvent(UserProfileOptionsDto userProfileOptionsDto) {
        log.info("Received user [{}] profile data event to update: {}", userProfileOptionsDto.getUser(),
                userProfileOptionsDto);
        userProfileOptionsDataService.createOrUpdateUserProfileOptions(userProfileOptionsDto);
        log.info("User [{}] profile data event to update has been processed: {}", userProfileOptionsDto.getUser(),
                userProfileOptionsDto);
    }
}
