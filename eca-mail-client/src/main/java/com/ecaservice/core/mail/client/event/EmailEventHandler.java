package com.ecaservice.core.mail.client.event;

import com.ecaservice.core.mail.client.event.model.EmailEvent;
import com.ecaservice.core.mail.client.mapping.EmailRequestMapper;
import com.ecaservice.core.mail.client.service.EmailRequestSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;


/**
 * Email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventHandler {

    private final EmailRequestSender emailRequestSender;
    private final EmailRequestMapper emailRequestMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handles email event.
     *
     * @param emailEvent - email event
     */
    @EventListener
    public void handleEmailEvent(EmailEvent emailEvent) {
        var emailRequest = emailEvent.getEmailRequest();
        log.info("Received email event [{}] from source [{}]", emailRequest.getTemplateCode(),
                emailEvent.getSource().getClass().getSimpleName());
        try {
            var emailRequestEntity = emailRequestMapper.map(emailRequest);
            var expiredAt = Optional.ofNullable(emailEvent.getRequestCacheDurationInMinutes())
                    .map(duration -> LocalDateTime.now().plusMinutes(duration))
                    .orElse(null);
            emailRequestEntity.setExpiredAt(expiredAt);
            emailRequestEntity.setRequestJson(objectMapper.writeValueAsString(emailRequest));
            emailRequestSender.sendEmail(emailRequest, emailRequestEntity);
        } catch (Exception ex) {
            log.error("There was an error while sent email request [{}]: {}", emailRequest.getTemplateCode(),
                    ex.getMessage());
        }
    }
}
