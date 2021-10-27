package com.ecaservice.core.mail.client.event.listener;

import com.ecaservice.core.mail.client.event.model.EmailEvent;
import com.ecaservice.core.mail.client.service.EmailEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.ecaservice.core.mail.client.config.EcaMailClientAutoConfiguration.MAIL_CLIENT_THREAD_POOL_TASK_EXECUTOR;


/**
 * Email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "mail.client.async", havingValue = "true")
@RequiredArgsConstructor
public class AsyncEmailEventHandler {

    private final EmailEventService emailEventService;

    /**
     * Handles email event.
     *
     * @param emailEvent - email event
     */
    @Async(MAIL_CLIENT_THREAD_POOL_TASK_EXECUTOR)
    @EventListener
    public void handleEmailEvent(EmailEvent emailEvent) {
        emailEventService.handleEmailEvent(emailEvent);
    }
}
