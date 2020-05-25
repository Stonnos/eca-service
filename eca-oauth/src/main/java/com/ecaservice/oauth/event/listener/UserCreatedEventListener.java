package com.ecaservice.oauth.event.listener;

import com.ecaservice.oauth.event.model.UserCreatedEvent;
import com.ecaservice.oauth.service.mail.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event listener that occurs after user is created.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserCreatedEventListener {

    private final NotificationService notificationService;

    /**
     * Handles event to sent email about user creation.
     *
     * @param userCreatedEvent - user created event
     */
    @Async
    @EventListener
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        try {
            notificationService.notifyUserCreated(userCreatedEvent.getLogin(), userCreatedEvent.getPassword());
        } catch (Exception ex) {
            log.error("There was an error while sending email request for user created [{}]: {}",
                    userCreatedEvent.getLogin(), ex.getMessage());
        }
    }
}
