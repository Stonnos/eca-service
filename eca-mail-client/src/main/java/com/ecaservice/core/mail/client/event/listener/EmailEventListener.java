package com.ecaservice.core.mail.client.event.listener;

import com.ecaservice.core.mail.client.event.model.AbstractEmailEvent;
import com.ecaservice.core.mail.client.service.EmailEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Email event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailEventProcessor emailEventProcessor;

    /**
     * Handles email event.
     *
     * @param event - email event.
     */
    @SuppressWarnings("unchecked")
    @EventListener
    public void handleNotificationEvent(AbstractEmailEvent event) {
        log.info("Received email event [{}] from source [{}]", event.getClass().getSimpleName(),
                event.getSource().getClass().getSimpleName());
        emailEventProcessor.handleEmailEvent(event);
    }
}
