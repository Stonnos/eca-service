package com.ecaservice.auto.test.message.handler;

import com.ecaservice.auto.test.model.EmailMessage;
import com.ecaservice.auto.test.service.EmailMessageParser;
import com.ecaservice.auto.test.service.EmailMessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * Email message handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailMessageHandler implements MessageHandler {

    private final EmailMessageParser emailMessageParser;
    private final EmailMessageProcessor emailMessageProcessor;

    @Override
    public void handleMessage(Message<?> message) {
        log.info("Starting to handle email message");
        MimeMessage mimeMessage = (MimeMessage) message.getPayload();
        try {
            EmailMessage emailMessage = emailMessageParser.parse(mimeMessage);
            emailMessageProcessor.processMessage(emailMessage);
        } catch (Exception ex) {
            log.error("There was an error while handling email message: {}", message);
        }
    }
}
