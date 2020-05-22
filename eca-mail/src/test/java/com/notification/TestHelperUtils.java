package com.notification;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailTemplateType;
import com.notification.config.MailConfig;
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
        emailRequest.setReceiver(RECEIVER_MAIL_RU);
        emailRequest.setTemplateType(EmailTemplateType.NEW_EXPERIMENT_TEMPLATE);
        emailRequest.setHtml(true);
        return emailRequest;
    }

    /**
     * Create mail config.
     *
     * @return mail config
     */
    public static MailConfig createMailConfig() {
        MailConfig mailConfig = new MailConfig();
        mailConfig.setSender(SENDER_MAIL_RU);
        mailConfig.setSubject(SUBJECT);
        return mailConfig;
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
