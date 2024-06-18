package com.ecaservice.mail.service;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.mail.exception.DuplicateRequestIdException;
import com.ecaservice.mail.mapping.EmailRequestMapper;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.mail.repository.EmailRepository;
import com.ecaservice.mail.repository.TemplateRepository;
import com.ecaservice.mail.service.template.TemplateProcessorService;
import com.ecaservice.mail.validation.annotations.ValidEmailRequest;
import com.ecaservice.notification.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

/**
 * Email service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class EmailService {

    private final MailConfig mailConfig;
    private final EmailRequestMapper emailRequestMapper;
    private final TemplateProcessorService templateProcessorService;
    private final EncryptorBase64AdapterService encryptorBase64AdapterService;
    private final EmailRepository emailRepository;
    private final TemplateRepository templateRepository;

    /**
     * Saves email request.
     *
     * @param emailRequest - email request
     * @return email response
     */
    @Locked(lockName = "saveEmail", key = "#emailRequest.requestId")
    public Email saveEmail(@ValidEmailRequest EmailRequest emailRequest) {
        log.info("Received email request with uuid [{}], correlation id [{}].", emailRequest.getRequestId(),
                emailRequest.getCorrelationId());
        TemplateEntity templateEntity = templateRepository.findByCode(emailRequest.getTemplateCode())
                .orElseThrow(() -> new EntityNotFoundException(TemplateEntity.class, emailRequest.getTemplateCode()));
        if (emailRepository.existsByUuid(emailRequest.getRequestId())) {
            throw new DuplicateRequestIdException(emailRequest.getRequestId());
        }
        Email email = emailRequestMapper.map(emailRequest, mailConfig);
        email.setSubject(templateEntity.getSubject());
        String message = templateProcessorService.process(emailRequest.getTemplateCode(), emailRequest.getVariables());
        String encodedMessage = encryptorBase64AdapterService.encrypt(message);
        email.setMessage(encodedMessage);
        email.setUuid(emailRequest.getRequestId());
        email.setTxId(emailRequest.getCorrelationId());
        email.setSaveDate(LocalDateTime.now());
        emailRepository.save(email);
        log.info("Email request with uuid [{}], correlation id [{}] has been saved.", emailRequest.getRequestId(),
                emailRequest.getCorrelationId());
        return email;
    }
}
