package com.ecaservice.oauth.event.listener;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.oauth.event.listener.handler.AbstractNotificationEventHandler;
import com.ecaservice.oauth.event.model.AbstractNotificationEvent;
import com.ecaservice.oauth.service.mail.EmailClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
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

    private final EmailClient emailClient;
    private final List<AbstractNotificationEventHandler> notificationEventHandlers;

    /**
     * Handles notification event.
     *
     * @param notificationEvent - notification event.
     */
    @SuppressWarnings("unchecked")
    @Async
    @EventListener
    public void handleNotificationEvent(AbstractNotificationEvent notificationEvent) {
        AbstractNotificationEventHandler eventHandler = notificationEventHandlers.stream()
                .filter(handler -> handler.canHandle(notificationEvent))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't handle notification event with type %s",
                                notificationEvent.getClass().getSimpleName())));
        EmailRequest emailRequest = eventHandler.handle(notificationEvent);
        log.info("Starting to sent email template [{}] to [{}]", emailRequest.getTemplateCode(),
                emailRequest.getReceiver());
        EmailResponse emailResponse = emailClient.sendEmail(emailRequest);
        log.info("Email request [{}] has been successfully sent for user [{}] with response id [{}]",
                emailRequest.getTemplateCode(), emailRequest.getReceiver(), emailResponse.getRequestId());
    }
}
