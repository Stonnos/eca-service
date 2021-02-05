package com.ecaservice.mail.service;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.mail.mapping.EmailRequestMapper;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

/**
 * Email service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final MailConfig mailConfig;
    private final EmailRequestMapper emailRequestMapper;
    private final TemplateEngineService templateEngineService;
    private final EmailRepository emailRepository;

    /**
     * Saves email request.
     *
     * @param emailRequest - email request
     * @return email response
     */
    public Email saveEmail(EmailRequest emailRequest) {
        String uuid = UUID.randomUUID().toString();
        log.info("Received email request with uuid '{}'.", uuid);
        Email email = emailRequestMapper.map(emailRequest, mailConfig);
        email.setMessage(buildEmailMessage(emailRequest));
        email.setUuid(uuid);
        email.setSaveDate(LocalDateTime.now());
        emailRepository.save(email);
        log.info("Email request with uuid '{}' has been saved.", uuid);
        return email;
    }

    private String buildEmailMessage(EmailRequest emailRequest) {
        String template = mailConfig.getMessageTemplatesMap().get(emailRequest.getTemplateType());
        Context context = new Context(Locale.getDefault(), emailRequest.getEmailMessageVariables());
        return templateEngineService.process(template, context);
    }
}
