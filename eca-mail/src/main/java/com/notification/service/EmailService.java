package com.notification.service;

import com.notification.dto.EmailRequest;
import com.notification.dto.EmailResponse;
import com.notification.mapping.EmailRequestMapper;
import com.notification.model.Email;
import com.notification.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.notification.util.Utils.buildResponse;

/**
 * Email service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailRepository emailRepository;
    private final EmailRequestMapper emailRequestMapper;

    /**
     * Saves email request.
     *
     * @param emailRequest - email request
     * @return email response
     */
    public EmailResponse saveEmail(EmailRequest emailRequest) {
        String uuid = UUID.randomUUID().toString();
        log.info("Received email request with uuid '{}'.", uuid);
        Email email = emailRequestMapper.map(emailRequest);
        email.setUuid(uuid);
        email.setSaveDate(LocalDateTime.now());
        emailRepository.save(email);
        log.info("Email request with uuid '{}' has been saved.", uuid);
        return buildResponse(UUID.randomUUID().toString());
    }
}
