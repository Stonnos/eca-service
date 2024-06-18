package com.ecaservice.oauth.scheduler;

import com.ecaservice.oauth.service.UserProfileOptionsDataEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * User profile options data event scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileOptionsDataEventScheduler {

    private final UserProfileOptionsDataEventProcessor userProfileOptionsDataEventProcessor;

    /**
     * Sent user profile options data events to mq.
     */
    @Scheduled(fixedDelayString = "${userProfile.dataEventRetryIntervalSeconds}000")
    public void sendEvents() {
        userProfileOptionsDataEventProcessor.sendEvents();
    }
}
