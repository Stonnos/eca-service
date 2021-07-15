package com.ecaservice.mail.controller;

import com.ecaservice.mail.mapping.EmailRequestMapper;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.service.EmailService;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Implements REST API for email application.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "API for email sending")
@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final EmailRequestMapper emailRequestMapper;

    /**
     * Saves email request.
     *
     * @param emailRequest - email request
     * @return email response
     */
    @Operation(description = "Saves email request to sent", summary = "Saves email request to sent")
    @PostMapping(value = "/email-request")
    public EmailResponse saveRequest(@Valid @RequestBody EmailRequest emailRequest) {
        Email email = emailService.saveEmail(emailRequest);
        return emailRequestMapper.map(email);
    }
}
