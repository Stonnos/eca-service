package com.ecaservice.oauth.service;

import com.ecaservice.core.transactional.outbox.annotation.TransactionalOutbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * User profile options data event sender.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@TransactionalOutbox
@RequiredArgsConstructor
public class UserProfileOptionsDataEventSender {
    //TODO impl user created event
   // private final UserProfileProperties userProfileProperties;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Sends user profile options data event to mq.
     *
     * @param userProfileOptionsDto - user profile options data event
     */
   /* @OutboxSender(USER_PROFILE_OPTIONS_DATA)
    public void send(UserProfileOptionsDto userProfileOptionsDto) {
        log.info("Starting to send user [{}] profile options data event to mq: [{}]", userProfileOptionsDto.getUser(),
                userProfileOptionsDto);
        rabbitTemplate.convertAndSend(userProfileProperties.getRabbit().getExchangeName(), StringUtils.EMPTY,
                userProfileOptionsDto);
        log.info("User [{}] profile options data event has been sent to mq: [{}]", userProfileOptionsDto.getUser(),
                userProfileOptionsDto);
    }*/
}
