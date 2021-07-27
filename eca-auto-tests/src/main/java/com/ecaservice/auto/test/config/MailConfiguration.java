package com.ecaservice.auto.test.config;

import com.ecaservice.auto.test.model.EmailMessage;
import com.ecaservice.auto.test.service.EmailMessageParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.integration.mail.support.DefaultMailHeaderMapper;
import org.springframework.integration.mapping.HeaderMapper;

import javax.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Mail configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableIntegration
@RequiredArgsConstructor
public class MailConfiguration {

    private final EmailMessageParser emailMessageParser;

    /**
     * Creates mail header mapper.
     *
     * @return mail header mapper bean
     */
    @Bean
    public HeaderMapper<MimeMessage> mailHeaderMapper() {
        return new DefaultMailHeaderMapper();
    }

    /**
     * Creates IMAP listener adapter integration flow.
     *
     * @return IMAP listener adapter integration flow
     */
    @Bean
    public IntegrationFlow iMapIdleFlow() {
        return IntegrationFlows
                .from(Mail.imapIdleAdapter(getUrl())
                        .autoStartup(true)
                        .autoCloseFolder(false)
                        .shouldMarkMessagesAsRead(true)
                ).handle(message -> {
                    try {
                        EmailMessage emailMessage = emailMessageParser.parse(message);
                    } catch (Exception ex) {
                        log.error("Email message parse error: {}", ex.getMessage());
                    }
                }).get();
    }

    private String getUrl() {
        String login = "rbatsw@gmail.com";
        String password = "";
        return String.format("imaps://%s:%s@imap.gmail.com:993/inbox",
                URLEncoder.encode(login, StandardCharsets.UTF_8), password);
    }
}
