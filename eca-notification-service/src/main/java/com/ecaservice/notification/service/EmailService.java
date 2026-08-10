package com.ecaservice.notification.service;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.notification.config.MailProperties;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.entity.Email;
import com.ecaservice.notification.entity.TemplateEntity;
import com.ecaservice.notification.exception.DuplicateRequestIdException;
import com.ecaservice.notification.mapping.EmailRequestMapper;
import com.ecaservice.notification.repository.EmailRepository;
import com.ecaservice.notification.repository.TemplateRepository;
import com.ecaservice.notification.service.template.TemplateProcessorService;
import com.ecaservice.notification.validation.annotations.ValidEmailRequest;
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

    private final MailProperties mailProperties;
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
    public Email saveEmail(@ValidEmailRequest EmailRequest emailRequest) {
        log.info("Received email request with uuid [{}], correlation id [{}], template code [{}].",
                emailRequest.getRequestId(), emailRequest.getCorrelationId(), emailRequest.getTemplateCode());
        TemplateEntity templateEntity = templateRepository.findByCode(emailRequest.getTemplateCode())
                .orElseThrow(() -> new EntityNotFoundException(TemplateEntity.class, emailRequest.getTemplateCode()));
        if (emailRepository.existsByUuid(emailRequest.getRequestId())) {
            throw new DuplicateRequestIdException(emailRequest.getRequestId());
        }
        Email email = emailRequestMapper.map(emailRequest, mailProperties);
        email.setSubject(templateEntity.getSubject());
        String message = templateProcessorService.process(emailRequest.getTemplateCode(), emailRequest.getVariables());
        String encodedMessage = encryptorBase64AdapterService.encrypt(message);
        email.setMessage(encodedMessage);
        email.setUuid(emailRequest.getRequestId());
        email.setTxId(emailRequest.getCorrelationId());
        email.setSaveDate(LocalDateTime.now());
        emailRepository.save(email);
        log.info("Email request with uuid [{}], correlation id [{}], template code [{}] has been saved.",
                emailRequest.getRequestId(), emailRequest.getCorrelationId(), emailRequest.getTemplateCode());
        return email;
    }
}
