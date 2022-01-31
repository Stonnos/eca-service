package com.ecaservice.core.mail.client.service;

import com.ecaservice.core.mail.client.entity.EmailRequestEntity;
import com.ecaservice.core.mail.client.entity.EmailRequestStatus;
import com.ecaservice.core.mail.client.repository.EmailRequestRepository;
import com.ecaservice.notification.dto.EmailRequest;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailRequestSender {

    private final EmailClient emailClient;
    private final EmailRequestRepository emailRequestRepository;

    /**
     * Sends email request to eca-mail service.
     *
     * @param emailRequest       - email request
     * @param emailRequestEntity - email request entity
     */
    public void sendEmail(EmailRequest emailRequest, EmailRequestEntity emailRequestEntity) {
        log.info("Starting to sent email request with code [{}]", emailRequest.getTemplateCode());
        try {
            var emailResponse = emailClient.sendEmail(emailRequest);
            emailRequestEntity.setRequestId(emailResponse.getRequestId());
            emailRequestEntity.setRequestStatus(EmailRequestStatus.SENT);
            emailRequestEntity.setSentDate(LocalDateTime.now());
            log.info("Email [{}] has been sent with request id [{}]", emailRequest.getTemplateCode(),
                    emailResponse.getRequestId());
        } catch (FeignException.ServiceUnavailable | RetryableException ex) {
            log.error("Service unavailable error while sending email request [{}]: {}",
                    emailRequest.getTemplateCode(), ex.getMessage());
            emailRequestEntity.setRequestStatus(EmailRequestStatus.NOT_SENT);
            emailRequestEntity.setDetails(ex.getMessage());
        } catch (Exception ex) {
            log.error("There was an error [{}] while sending email request [{}]: {}", ex.getClass().getSimpleName(),
                    emailRequest.getTemplateCode(), ex.getMessage());
            emailRequestEntity.setRequestStatus(EmailRequestStatus.ERROR);
            emailRequestEntity.setDetails(ex.getMessage());
        } finally {
            emailRequestRepository.save(emailRequestEntity);
        }
    }
}
