package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.config.ResetPasswordConfig;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.event.model.ResetPasswordNotificationEvent;
import com.ecaservice.oauth.service.UserService;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.RESET_PASSWORD_URL_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.VALIDITY_MINUTES_KEY;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements reset password notification event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ResetPasswordNotificationEventHandler
        extends AbstractNotificationEventHandler<ResetPasswordNotificationEvent> {

    private static final String RESET_PASSWORD_URL_FORMAT = "%s/reset-password/?token=%s";

    private final ResetPasswordConfig resetPasswordConfig;
    private final UserService userService;

    /**
     * Creates reset password notification event handler.
     *
     * @param resetPasswordConfig - reset password config
     * @param userService         - user service bean
     */
    public ResetPasswordNotificationEventHandler(ResetPasswordConfig resetPasswordConfig,
                                                 UserService userService) {
        super(ResetPasswordNotificationEvent.class, Templates.RESET_PASSWORD);
        this.resetPasswordConfig = resetPasswordConfig;
        this.userService = userService;
    }

    @Override
    Map<String, String> createVariables(ResetPasswordNotificationEvent event) {
        Map<String, String> templateVariables = newHashMap();
        String resetPasswordUrl = String.format(RESET_PASSWORD_URL_FORMAT, resetPasswordConfig.getBaseUrl(),
                event.getTokenModel().getToken());
        templateVariables.put(RESET_PASSWORD_URL_KEY, resetPasswordUrl);
        templateVariables.put(VALIDITY_MINUTES_KEY, String.valueOf(resetPasswordConfig.getValidityMinutes()));
        return templateVariables;
    }

    @Override
    String getReceiver(ResetPasswordNotificationEvent event) {
        UserEntity userEntity = userService.getById(event.getTokenModel().getUserId());
        return userEntity.getEmail();
    }
}
