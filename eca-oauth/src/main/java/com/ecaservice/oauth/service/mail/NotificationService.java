package com.ecaservice.oauth.service.mail;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.notification.dto.EmailTemplateType;
import com.ecaservice.oauth.config.ResetPasswordConfig;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.PASSWORD_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.RESET_PASSWORD_URL_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.USERNAME_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.VALIDITY_MINUTES_KEY;
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

    private final ResetPasswordConfig resetPasswordConfig;
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
        notifyByEmail(userEntity, templateVariables, EmailTemplateType.NEW_USER_TEMPLATE);
    }

    /**
     * Sends email with reset password link.
     *
     * @param resetPasswordRequestEntity - reset password request entity
     */
    public void sendResetPasswordLink(ResetPasswordRequestEntity resetPasswordRequestEntity) {
        log.info("Starting to send reset password link for user [{}].",
                resetPasswordRequestEntity.getUserEntity().getEmail());
        String resetPasswordUrl = String.format(resetPasswordConfig.getResetPasswordUrl(),
                resetPasswordRequestEntity.getToken());
        Map<String, Object> templateVariables = newHashMap();
        templateVariables.put(RESET_PASSWORD_URL_KEY, resetPasswordUrl);
        templateVariables.put(VALIDITY_MINUTES_KEY, resetPasswordConfig.getValidityMinutes());
        notifyByEmail(resetPasswordRequestEntity.getUserEntity(), templateVariables,
                EmailTemplateType.RESET_PASSWORD_TEMPLATE);
    }

    private void notifyByEmail(UserEntity userEntity, Map<String, Object> variables,
                               EmailTemplateType emailTemplateType) {
        try {
            EmailRequest emailRequest = createEmailRequest(userEntity, variables, emailTemplateType);
            EmailResponse emailResponse = emailClient.sendEmail(emailRequest);
            log.info("Email request [{}] has been successfully sent for user [{}] with response id [{}]",
                    emailTemplateType, userEntity.getEmail(), emailResponse.getRequestId());
        } catch (Exception ex) {
            log.error("There was an error while sending email request [{}] for user [{}]: {}", emailTemplateType,
                    userEntity.getEmail(), ex.getMessage());
        }
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
