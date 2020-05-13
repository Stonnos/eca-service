package com.notification;

import com.ecaservice.notification.dto.EmailRequest;
import com.notification.model.Email;
import com.notification.model.EmailStatus;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String MESSAGE = "message";
    private static final String SUBJECT = "subject";
    private static final String SENDER_MAIL_RU = "sender@mail.ru";
    private static final String RECEIVER_MAIL_RU = "receiver@mail.ru";

    /**
     * Creates email request.
     *
     * @return email request
     */
    public static EmailRequest createEmailRequest() {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setMessage(MESSAGE);
        emailRequest.setSubject(SUBJECT);
        emailRequest.setSender(SENDER_MAIL_RU);
        emailRequest.setReceiver(RECEIVER_MAIL_RU);
        emailRequest.setHtml(true);
        return emailRequest;
    }

    /**
     * Creates email.
     *
     * @param saveDate - save date
     * @param status   - email status
     * @return email object
     */
    public static Email createEmail(LocalDateTime saveDate, EmailStatus status) {
        Email email = new Email();
        email.setUuid(UUID.randomUUID().toString());
        email.setSaveDate(saveDate);
        email.setStatus(status);
        email.setSender(SENDER_MAIL_RU);
        email.setReceiver(RECEIVER_MAIL_RU);
        email.setSubject(SUBJECT);
        email.setMessage(MESSAGE);
        email.setHtml(true);
        return email;
    }
}
