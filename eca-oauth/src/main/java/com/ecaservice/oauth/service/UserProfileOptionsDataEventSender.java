package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.UserProfileProperties;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * User profile options data event sender.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileOptionsDataEventSender {

    private final UserProfileProperties userProfileProperties;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Sends user profile options data event to mq.
     *
     * @param userProfileOptionsDto - user profile options data event
     */
    public void send(UserProfileOptionsDto userProfileOptionsDto) {
        log.info("Starting to send user [{}] profile options data event to mq: [{}]", userProfileOptionsDto.getUser(),
                userProfileOptionsDto);
        rabbitTemplate.convertAndSend(userProfileProperties.getRabbit().getExchangeName(), StringUtils.EMPTY,
                userProfileOptionsDto);
        log.info("User [{}] profile options data event has been sent to mq: [{}]", userProfileOptionsDto.getUser(),
                userProfileOptionsDto);
    }
}
