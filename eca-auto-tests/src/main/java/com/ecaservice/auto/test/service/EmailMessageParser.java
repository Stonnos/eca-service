package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.model.EmailMessage;
import com.ecaservice.auto.test.model.EmailType;
import com.ecaservice.auto.test.model.EmailTypeVisitor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements email message parser.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EmailMessageParser {

    private static final String REQUEST_ID_REGEX =
            "[0-9a-f]{8}-[0-9a-f]{4}-[34][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
    private static final String URL_REGEX = "\"http://.*\"";
    private static final String QUOTE = "\"";

    /**
     * Parse email message.
     *
     * @param message - input message
     * @return email message model
     * @throws MessagingException in case of messaging errors
     * @throws IOException        in case of I/O errors
     */
    public EmailMessage parse(Message<?> message) throws MessagingException, IOException {
        log.info("Starting to parse email message");
        MimeMessage mimeMessage = (MimeMessage) message.getPayload();
        String content = String.valueOf(mimeMessage.getContent());
        EmailType emailType = getEmailType(mimeMessage);
        String requestId = getRequestId(content);
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setEmailType(emailType);
        emailMessage.setRequestId(requestId);
        emailMessage.getEmailType().handle(new EmailTypeVisitor() {
            @Override
            public void visitFinishedExperiment() {
                String downloadUrl = getDownloadUrl(content);
                emailMessage.setDownloadUrl(downloadUrl);
            }
        });
        log.info("Got email message result model: {}", emailMessage);
        return emailMessage;
    }

    private EmailType getEmailType(MimeMessage mimeMessage) throws MessagingException {
        String subject = mimeMessage.getSubject();
        return EmailType.findByDescription(subject);
    }

    private String getRequestId(String content) {
        return getValueByRegex(REQUEST_ID_REGEX, content,
                () -> new IllegalArgumentException("Can't find requestId value in message content!"));
    }

    private String getDownloadUrl(String content) {
        String value = getValueByRegex(URL_REGEX, content,
                () -> new IllegalArgumentException("Can't find downloadUrl value in message content!"));
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
