package com.ecaservice.auto.test.messaging.transformer;

import com.ecaservice.auto.test.exception.EmailParseException;
import com.ecaservice.auto.test.model.EmailMessage;
import com.ecaservice.auto.test.model.EmailType;
import com.ecaservice.auto.test.model.EmailTypeVisitor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ecaservice.auto.test.config.mail.Channels.MAIL_HANDLE_CHANNEL;
import static com.ecaservice.auto.test.config.mail.Channels.MAIL_TRANSFORM_CHANNEL;

/**
 * Email transformer service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "mail.enabled", havingValue = "true")
public class EmailMessageTransformer {

    private static final String REQUEST_ID_REGEX =
            "[0-9a-f]{8}-[0-9a-f]{4}-[34][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    private static final String URL_REGEX = "\"http://.*\"";
    private static final String QUOTE = "\"";

    /**
     * Transforms message to payload.
     *
     * @param message - message object
     * @return email message payload
     * @throws MessagingException in case of messaging exception
     * @throws IOException        in case of I/O errors
     */
    @Transformer(inputChannel = MAIL_TRANSFORM_CHANNEL, outputChannel = MAIL_HANDLE_CHANNEL)
    public EmailMessage transform(Message<?> message) throws MessagingException, IOException {
        log.info("Starting to transform message: {}", message);
        MimeMessage payload = (MimeMessage) message.getPayload();
        String subject = payload.getSubject();
        String content = String.valueOf(payload.getContent());
        EmailType emailType = EmailType.findByDescription(subject);
        String requestId = getRequestId(content);
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setEmailType(emailType);
        emailMessage.setRequestId(requestId);
        populateAdditionalFields(emailMessage, content);
        log.info("Got email message result model: {}", emailMessage);
        return emailMessage;
    }

    private void populateAdditionalFields(EmailMessage emailMessage, String content) {
        emailMessage.getEmailType().handle(new EmailTypeVisitor() {
            @Override
            public void visitFinishedExperiment() {
                String downloadUrl = getDownloadUrl(content);
                emailMessage.setDownloadUrl(downloadUrl);
            }
        });
    }

    private String getRequestId(String content) {
        return getValueByRegex(REQUEST_ID_REGEX, content,
                () -> new EmailParseException("Can't find requestId value in message content!"));
    }

    private String getDownloadUrl(String content) {
        String value = getValueByRegex(URL_REGEX, content,
                () -> new EmailParseException("Can't find downloadUrl value in message content!"));
        return StringUtils.remove(value, QUOTE);
    }

    private String getValueByRegex(String regex, String content,
                                   Supplier<? extends RuntimeException> exceptionSupplier) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            throw exceptionSupplier.get();
        } else {
            return matcher.group();
        }
    }
}
