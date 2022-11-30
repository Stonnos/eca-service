package com.ecaservice.core.mail.client.event.listener.handler;

import com.ecaservice.core.mail.client.event.model.AbstractEmailEvent;
import com.ecaservice.notification.dto.EmailRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static com.ecaservice.notification.util.Priority.MEDIUM;

/**
 * Abstract email event handler.
 *
 * @param <T> - email event type
 * @author Roman Batygin
 */
@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEmailEventHandler<T extends AbstractEmailEvent> {

    @Getter
    private final Class<T> type;
    @Getter
    private final String templateCode;

    /**
     * Gets receiver.
     *
     * @return receiver
     */
    public abstract String getReceiver();

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
        emailRequest.setRequestId(UUID.randomUUID().toString());
        emailRequest.setTemplateCode(getTemplateCode());
        emailRequest.setReceiver(getReceiver());
        emailRequest.setVariables(createVariables(event));
        emailRequest.setPriority(getPriority());
        return emailRequest;
    }

    /**
     * Gets delivery priority.
     *
     * @return delivery priority
     */
    public int getPriority() {
        return MEDIUM;
    }

    /**
     * Creates email template variables map.
     *
     * @param event - notification event
     * @return variables map
     */
    public Map<String, String> createVariables(T event) {
        return Collections.emptyMap();
    }
}
