package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.oauth.event.model.AbstractNotificationEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.ecaservice.notification.util.Priority.MEDIUM;

/**
 * Abstract notification event handler.
 *
 * @param <T> - notification event type
 * @author Roman Batygin
 */
@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractNotificationEventHandler<T extends AbstractNotificationEvent> {

    @Getter
    private final Class<T> type;
    @Getter
    private final String templateCode;

    /**
     * Can handle notification event?
     *
     * @param event - notification event
     * @return {@code true} if class can handle notification event
     */
    public boolean canHandle(T event) {
        return event != null && type.isAssignableFrom(event.getClass());
    }

    /**
     * Handles notification event and creates email request object.
     *
     * @param event - notification event.
     */
    public EmailRequest handle(T event) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTemplateCode(getTemplateCode());
        emailRequest.setReceiver(getReceiver(event));
        emailRequest.setVariables(createVariables(event));
        emailRequest.setPriority(getPriority());
        return emailRequest;
    }

    /**
     * Gets delivery priority.
     *
     * @return delivery priority
     */
    int getPriority() {
        return MEDIUM;
    }

    /**
     * Creates email template variables map.
     *
     * @param event - notification event
     * @return variables map
     */
    abstract Map<String, String> createVariables(T event);

    /**
     * Gets receiver from event.
     *
     * @param event -  notification event
     * @return receiver
     */
    abstract String getReceiver(T event);
}
