package com.ecaservice.core.mail.client.service;

import com.ecaservice.core.mail.client.event.listener.handler.AbstractEmailEventHandler;
import com.ecaservice.core.mail.client.event.model.AbstractEmailEvent;
import com.ecaservice.notification.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleEmailEventProcessor implements EmailEventProcessor {

    private final EmailRequestSender emailRequestSender;
    private final List<AbstractEmailEventHandler> emailEventHandlers;

    @Override
    @SuppressWarnings("unchecked")
    public void handleEmailEvent(AbstractEmailEvent event) {
        log.info("Starting to handle email event [{}]", event.getClass().getSimpleName());
        var eventHandler = emailEventHandlers.stream()
                .filter(handler -> handler.canHandle(event))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't handle email event with type %s",
                                event.getClass().getSimpleName())));
        EmailRequest emailRequest = eventHandler.handle(event);
        sendEmail(emailRequest);
        log.info("Email event [{}] has been processed", event.getClass().getSimpleName());
    }

    private void sendEmail(EmailRequest emailRequest) {
        try {
            emailRequestSender.sendEmail(emailRequest);
        } catch (Exception ex) {
            log.error("There was an error while sent email request [{}]: {}", emailRequest.getTemplateCode(),
                    ex.getMessage());
        }
    }
}
