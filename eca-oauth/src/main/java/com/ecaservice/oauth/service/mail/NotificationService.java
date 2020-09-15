package com.ecaservice.oauth.service.mail;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.notification.dto.EmailType;
import com.ecaservice.oauth.config.ResetPasswordConfig;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.PASSWORD_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.RESET_PASSWORD_URL_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.TFA_CODE;
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

    private static final String RESET_PASSWORD_URL_FORMAT = "%s/reset-password/?token=%s";

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
        notifyByEmail(userEntity, templateVariables, EmailType.NEW_USER);
    }

    /**
     * Sends email with reset password link.
     *
     * @param resetPasswordRequestEntity - reset password request entity
     */
    public void sendResetPasswordLink(ResetPasswordRequestEntity resetPasswordRequestEntity) {
        log.info("Starting to send reset password link for user [{}].",
                resetPasswordRequestEntity.getUserEntity().getEmail());
        String resetPasswordUrl = String.format(RESET_PASSWORD_URL_FORMAT, resetPasswordConfig.getBaseUrl(),
                resetPasswordRequestEntity.getToken());
        Map<String, Object> templateVariables = newHashMap();
        templateVariables.put(RESET_PASSWORD_URL_KEY, resetPasswordUrl);
        templateVariables.put(VALIDITY_MINUTES_KEY, resetPasswordConfig.getValidityMinutes());
        notifyByEmail(resetPasswordRequestEntity.getUserEntity(), templateVariables,
                EmailType.RESET_PASSWORD);
    }

    /**
     * Sends email with two factor authentication code.
     *
     * @param userEntity - user entity
     * @param code       - code value
     */
    public void sendTfaCode(UserEntity userEntity, String code) {
        log.info("Starting to send tfa code for user [{}].", userEntity.getEmail());
        Map<String, Object> templateVariables = Collections.singletonMap(TFA_CODE, code);
        notifyByEmail(userEntity, templateVariables, EmailType.TFA_CODE);
    }

    private void notifyByEmail(UserEntity userEntity, Map<String, Object> variables,
                               EmailType emailType) {
        try {
            EmailRequest emailRequest = createEmailRequest(userEntity, variables, emailType);
            EmailResponse emailResponse = emailClient.sendEmail(emailRequest);
            log.info("Email request [{}] has been successfully sent for user [{}] with response id [{}]",
                    emailType, userEntity.getEmail(), emailResponse.getRequestId());
        } catch (Exception ex) {
            log.error("There was an error while sending email request [{}] for user [{}]: {}", emailType,
                    userEntity.getEmail(), ex.getMessage());
        }
    }

    private EmailRequest createEmailRequest(UserEntity userEntity, Map<String, Object> variables,
                                            EmailType emailType) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setReceiver(userEntity.getEmail());
        emailRequest.setTemplateType(emailType);
        emailRequest.setEmailMessageVariables(variables);
        emailRequest.setHtml(true);
        return emailRequest;
    }
}
