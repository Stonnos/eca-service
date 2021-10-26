package com.ecaservice.mail.service;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.mail.config.MailConfig;
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
import java.util.UUID;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.getMdc;

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
    public Email saveEmail(@ValidEmailRequest EmailRequest emailRequest) {
        String uuid = UUID.randomUUID().toString();
        log.info("Received email request with uuid '{}'.", uuid);
        TemplateEntity templateEntity = templateRepository.findByCode(emailRequest.getTemplateCode())
                .orElseThrow(() -> new EntityNotFoundException(TemplateEntity.class, emailRequest.getTemplateCode()));
        String txId = getMdc(TX_ID);
        Email email = emailRequestMapper.map(emailRequest, mailConfig);
        email.setSubject(templateEntity.getSubject());
        String message = templateProcessorService.process(emailRequest.getTemplateCode(), emailRequest.getVariables());
        String encodedMessage = encryptorBase64AdapterService.encrypt(message);
        email.setMessage(encodedMessage);
        email.setUuid(uuid);
        email.setTxId(txId);
        email.setSaveDate(LocalDateTime.now());
        emailRepository.save(email);
        log.info("Email request with uuid '{}' has been saved.", uuid);
        return email;
    }
}
