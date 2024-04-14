package com.ecaservice.oauth.event.listener;

import com.ecaservice.oauth.event.model.UserProfileOptionsDataEvent;
import com.ecaservice.oauth.service.UserProfileOptionsDataEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * User profile options data event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileOptionsDataEventListener {

    private final UserProfileOptionsDataEventProcessor userProfileOptionsDataEventProcessor;

    /**
     * Handles user profile options data event after transaction commited.
     *
     * @param userProfileOptionsDataEvent - user profile options data event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = UserProfileOptionsDataEvent.class)
    public void handleEvent(UserProfileOptionsDataEvent userProfileOptionsDataEvent) {
        log.info("Starting to handle user profile options data event committed from [{}]",
                userProfileOptionsDataEvent.getSource());
        userProfileOptionsDataEventProcessor.sendEvents();
        log.info("User [profile options data event committed from [{}] has been processed",
                userProfileOptionsDataEvent.getSource());
    }
}
