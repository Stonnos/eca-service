package com.notification.controller;

import com.notification.dto.EmailRequest;
import com.notification.dto.EmailResponse;
import com.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implements REST API for ECA application.
 *
 * @author Roman Batygin
 */
//@Api(tags = "API for ECA application")
@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping(value = "/email-request")
    public EmailResponse saveRequest(@RequestBody EmailRequest emailRequest) {
        return emailService.saveEmail(emailRequest);
    }
}
