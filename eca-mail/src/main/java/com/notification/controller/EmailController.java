package com.notification.controller;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.notification.mapping.EmailRequestMapper;
import com.notification.model.Email;
import com.notification.service.EmailService;
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
    @PostMapping(value = "/email-request")
    public EmailResponse saveRequest(@Valid @RequestBody EmailRequest emailRequest) {
        Email email = emailService.saveEmail(emailRequest);
        return emailRequestMapper.map(email);
    }
}
