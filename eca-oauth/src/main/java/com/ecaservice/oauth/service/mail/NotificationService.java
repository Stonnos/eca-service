package com.ecaservice.oauth.service.mail;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.notification.dto.EmailTemplateType;
import com.ecaservice.oauth.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.PASSWORD_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.USERNAME_KEY;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements service for notifications sending.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailClient emailClient;

    /**
     * Send email notification with created user details.
     *
     * @param userEntity - user entity
     * @param password   - user password
     */
    public void notifyUserCreated(UserEntity userEntity, String password) {
        log.info("Starting to send email request for new user [{}].", userEntity.getLogin());
        Map<String, Object> templateVariables = newHashMap();
        templateVariables.put(USERNAME_KEY, userEntity.getLogin());
        templateVariables.put(PASSWORD_KEY, password);
        EmailResponse emailResponse = notifyByEmail(userEntity, templateVariables, EmailTemplateType.NEW_USER_TEMPLATE);
        log.info("Email request [{}] has been successfully sent for new user [{}].", emailResponse.getRequestId(),
                userEntity.getLogin());
    }

    private EmailResponse notifyByEmail(UserEntity userEntity, Map<String, Object> variables,
                                        EmailTemplateType emailTemplateType) {
        EmailRequest emailRequest = createEmailRequest(userEntity, variables, emailTemplateType);
        return emailClient.sendEmail(emailRequest);
    }

    private EmailRequest createEmailRequest(UserEntity userEntity, Map<String, Object> variables,
                                            EmailTemplateType emailTemplateType) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setReceiver(userEntity.getEmail());
        emailRequest.setTemplateType(emailTemplateType);
        emailRequest.setEmailMessageVariables(variables);
        emailRequest.setHtml(true);
        return emailRequest;
    }
}
