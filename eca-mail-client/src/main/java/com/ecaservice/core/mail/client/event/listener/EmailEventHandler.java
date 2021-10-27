package com.ecaservice.core.mail.client.event.listener;

import com.ecaservice.core.mail.client.event.model.EmailEvent;
import com.ecaservice.core.mail.client.service.EmailEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


/**
 * Email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "mail.client.async", havingValue = "false", matchIfMissing = true)
@RequiredArgsConstructor
public class EmailEventHandler {

    private final EmailEventService emailEventService;

    /**
     * Handles email event.
     *
     * @param emailEvent - email event
     */
    @EventListener
    public void handleEmailEvent(EmailEvent emailEvent) {
        emailEventService.handleEmailEvent(emailEvent);
    }
}
