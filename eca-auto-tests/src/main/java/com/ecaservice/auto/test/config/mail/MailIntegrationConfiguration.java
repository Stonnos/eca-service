package com.ecaservice.auto.test.config.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.integration.mail.support.DefaultMailHeaderMapper;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.messaging.MessageChannel;

import javax.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.ecaservice.auto.test.config.mail.Channels.MAIL_HANDLE_CHANNEL;
import static com.ecaservice.auto.test.config.mail.Channels.MAIL_TRANSFORM_CHANNEL;

/**
 * Mail configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableIntegration
@EnableConfigurationProperties(MailProperties.class)
@RequiredArgsConstructor
public class MailIntegrationConfiguration {

    private static final String IMAPS_URL_FORMAT = "imaps://%s:%s@imap.gmail.com:993/inbox";

    private final MailProperties mailProperties;

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
     * Creates mail transform channel.
     *
     * @return channel bean
     */
    @Bean(name = MAIL_TRANSFORM_CHANNEL)
    public MessageChannel mailTransformChannel() {
        return new DirectChannel();
    }

    /**
     * Creates mail handle channel.
     *
     * @return channel bean
     */
    @Bean(name = MAIL_HANDLE_CHANNEL)
    public MessageChannel mailHandleChannel() {
        return new DirectChannel();
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
                        .outputChannel(mailTransformChannel())
                )
                .get();
    }

    private String getUrl() {
        return String.format(IMAPS_URL_FORMAT, URLEncoder.encode(mailProperties.getUserName(), StandardCharsets.UTF_8),
                mailProperties.getPassword());
    }
}
