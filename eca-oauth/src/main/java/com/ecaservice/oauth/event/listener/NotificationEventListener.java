package com.ecaservice.oauth.event.listener;

import com.ecaservice.core.mail.client.event.model.EmailEvent;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.oauth.event.listener.handler.AbstractNotificationEventHandler;
import com.ecaservice.oauth.event.model.AbstractNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Notification event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final List<AbstractNotificationEventHandler> notificationEventHandlers;

    /**
     * Handles notification event.
     *
     * @param notificationEvent - notification event.
     */
    @SuppressWarnings("unchecked")
    @EventListener
    public void handleNotificationEvent(AbstractNotificationEvent notificationEvent) {
        log.info("Received notification event [{}] from source [{}]", notificationEvent.getClass().getSimpleName(),
                notificationEvent.getSource().getClass().getSimpleName());
        var eventHandler = notificationEventHandlers.stream()
                .filter(handler -> handler.canHandle(notificationEvent))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't handle notification event with type %s",
                                notificationEvent.getClass().getSimpleName())));
        EmailRequest emailRequest = eventHandler.handle(notificationEvent);
        log.info("Starting to sent email template [{}] to [{}]", emailRequest.getTemplateCode(),
                emailRequest.getReceiver());
        EmailEvent emailEvent = new EmailEvent(this, emailRequest);
        applicationEventPublisher.publishEvent(emailEvent);
    }
}
