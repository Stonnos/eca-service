package com.ecaservice.core.mail.client.service;

import com.ecaservice.core.mail.client.event.model.EmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventService {

    private final EmailRequestSender emailRequestSender;

    /**
     * Handles email event.
     *
     * @param emailEvent - email event
     */
    public void handleEmailEvent(EmailEvent emailEvent) {
        var emailRequest = emailEvent.getEmailRequest();
        log.info("Received email event [{}] from source [{}]", emailRequest.getTemplateCode(),
                emailEvent.getSource().getClass().getSimpleName());
        try {
            emailRequestSender.sendEmail(emailRequest);
        } catch (Exception ex) {
            log.error("There was an error while sent email request [{}]: {}", emailRequest.getTemplateCode(),
                    ex.getMessage());
        }
    }
}
