package com.ecaservice.core.mail.client.service;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.core.mail.client.config.EcaMailClientProperties;
import com.ecaservice.core.mail.client.entity.EmailRequestEntity;
import com.ecaservice.core.mail.client.event.model.EmailEvent;
import com.ecaservice.core.mail.client.mapping.EmailRequestMapper;
import com.ecaservice.notification.dto.EmailRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class EmailEventService {

    private final EcaMailClientProperties ecaMailClientProperties;
    private final EmailRequestSender emailRequestSender;
    private final EmailRequestMapper emailRequestMapper;
    private final EncryptorBase64AdapterService encryptorBase64AdapterService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
            var emailRequestEntity = emailRequestMapper.map(emailRequest);
            var expiredAt = Optional.ofNullable(emailEvent.getRequestCacheDurationInMinutes())
                    .map(duration -> LocalDateTime.now().plusMinutes(duration))
                    .orElse(null);
            emailRequestEntity.setExpiredAt(expiredAt);
            setRequestJson(emailRequest, emailRequestEntity);
            emailRequestSender.sendEmail(emailRequest, emailRequestEntity);
        } catch (Exception ex) {
            log.error("There was an error while sent email request [{}]: {}", emailRequest.getTemplateCode(),
                    ex.getMessage());
        }
    }

    private void setRequestJson(EmailRequest emailRequest, EmailRequestEntity emailRequestEntity)
            throws JsonProcessingException {
        String requestJson = objectMapper.writeValueAsString(emailRequest);
        if (Boolean.TRUE.equals(ecaMailClientProperties.getEncrypt().getEnabled())) {
            String jsonRequestCipher = encryptorBase64AdapterService.encrypt(requestJson);
            emailRequestEntity.setRequestJson(jsonRequestCipher);
            emailRequestEntity.setEncrypted(true);
        } else {
            emailRequestEntity.setRequestJson(requestJson);
        }
    }
}
