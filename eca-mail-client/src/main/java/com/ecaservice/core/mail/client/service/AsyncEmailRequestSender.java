package com.ecaservice.core.mail.client.service;

import com.ecaservice.notification.dto.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import static com.ecaservice.core.mail.client.config.EcaMailClientAutoConfiguration.MAIL_CLIENT_THREAD_POOL_TASK_EXECUTOR;

/**
 * Async email request sender.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "mail.client.async", havingValue = "true")
@Primary
public class AsyncEmailRequestSender implements EmailRequestSender {

    private final EmailRequestSender emailRequestSender;

    /**
     * Constructor with parameters.
     *
     * @param emailRequestSender - email request sender
     */
    public AsyncEmailRequestSender(@Qualifier("simpleEmailRequestSender") EmailRequestSender emailRequestSender) {
        this.emailRequestSender = emailRequestSender;
    }

    /**
     * Init method.
     */
    @PostConstruct
    public void init() {
        log.info("Email client async sender has been configured");
    }

    @Override
    @Async(MAIL_CLIENT_THREAD_POOL_TASK_EXECUTOR)
    public void sendEmail(EmailRequest emailRequest) {
        emailRequestSender.sendEmail(emailRequest);
    }
}
