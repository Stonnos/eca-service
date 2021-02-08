package com.ecaservice.mail;

import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.model.EmailStatus;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.notification.dto.EmailRequest;
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
    private static final String TEST_TEMPLATE_CODE = "testTemplate";
    private static final String TEMPLATE_CODE = "testTemplate";
    private static final String TEMPLATE_DESCRIPTION = "Test template";

    /**
     * Creates email request.
     *
     * @return email request
     */
    public static EmailRequest createEmailRequest() {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setReceiver(RECEIVER_MAIL_RU);
        emailRequest.setTemplateCode(TEST_TEMPLATE_CODE);
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
        return email;
    }

    /**
     * Creates template entity.
     *
     * @return template entity
     */
    public static TemplateEntity createTemplateEntity() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setCode(TEMPLATE_CODE);
        templateEntity.setDescription(TEMPLATE_DESCRIPTION);
        templateEntity.setBody(MESSAGE);
        templateEntity.setSubject(SUBJECT);
        templateEntity.setCreated(LocalDateTime.now());
        return templateEntity;
    }
}
