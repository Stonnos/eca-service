package com.ecaservice.oauth.service.mail;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.oauth.config.ResetPasswordConfig;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
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
        Map<String, String> templateVariables = newHashMap();
        templateVariables.put(USERNAME_KEY, userEntity.getLogin());
        templateVariables.put(PASSWORD_KEY, password);
        notifyByEmail(userEntity, Templates.NEW_USER, templateVariables);
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
        Map<String, String> templateVariables = newHashMap();
        templateVariables.put(RESET_PASSWORD_URL_KEY, resetPasswordUrl);
        templateVariables.put(VALIDITY_MINUTES_KEY, String.valueOf(resetPasswordConfig.getValidityMinutes()));
        notifyByEmail(resetPasswordRequestEntity.getUserEntity(), Templates.RESET_PASSWORD, templateVariables);
    }

    /**
     * Sends email with two factor authentication code.
     *
     * @param userEntity - user entity
     * @param code       - code value
     */
    public void sendTfaCode(UserEntity userEntity, String code) {
        log.info("Starting to send tfa code for user [{}].", userEntity.getEmail());
        Map<String, String> templateVariables = Collections.singletonMap(TFA_CODE, code);
        notifyByEmail(userEntity, Templates.TFA_CODE, templateVariables);
    }

    private void notifyByEmail(UserEntity userEntity, String templateCode, Map<String, String> variables) {
        try {
            EmailRequest emailRequest = createEmailRequest(userEntity, templateCode, variables);
            EmailResponse emailResponse = emailClient.sendEmail(emailRequest);
            log.info("Email request [{}] has been successfully sent for user [{}] with response id [{}]",
                    templateCode, userEntity.getEmail(), emailResponse.getRequestId());
        } catch (Exception ex) {
            log.error("There was an error while sending email request [{}] for user [{}]: {}", templateCode,
                    userEntity.getEmail(), ex.getMessage());
        }
    }

    private EmailRequest createEmailRequest(UserEntity userEntity, String templateCode, Map<String, String> variables) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setReceiver(userEntity.getEmail());
        emailRequest.setTemplateCode(templateCode);
        emailRequest.setVariables(variables);
        return emailRequest;
    }
}
