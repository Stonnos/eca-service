package com.ecaservice.oauth.event.listener;

import com.ecaservice.oauth.event.model.ResetPasswordRequestCreatedEvent;
import com.ecaservice.oauth.service.mail.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Event listener that occurs after reset password request is created.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResetPasswordRequestCreatedEventListener {

    private final NotificationService notificationService;

    /**
     * Handles event to sent email with reset password link.
     *
     * @param event - reset password request created event
     */
    @Async
    @EventListener
    public void handleResetPasswordRequestCreatedEvent(ResetPasswordRequestCreatedEvent event) {

    }
}
