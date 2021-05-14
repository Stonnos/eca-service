package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.config.ChangePasswordConfig;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.event.model.ChangePasswordNotificationEvent;
import com.ecaservice.oauth.service.UserService;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.CHANGE_PASSWORD_URL_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.VALIDITY_MINUTES_KEY;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements change password notification event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ChangePasswordNotificationEventHandler
        extends AbstractNotificationEventHandler<ChangePasswordNotificationEvent> {

    private static final String CHANGE_PASSWORD_URL_FORMAT = "%s/change-password/?token=%s";

    private final ChangePasswordConfig changePasswordConfig;
    private final UserService userService;

    /**
     * Creates change password notification event handler.
     *
     * @param changePasswordConfig - change password config
     * @param userService          - user service bean
     */
    public ChangePasswordNotificationEventHandler(ChangePasswordConfig changePasswordConfig,
                                                  UserService userService) {
        super(ChangePasswordNotificationEvent.class, Templates.CHANGE_PASSWORD);
        this.changePasswordConfig = changePasswordConfig;
        this.userService = userService;
    }

    @Override
    Map<String, String> createVariables(ChangePasswordNotificationEvent event) {
        String changePasswordUrl = String.format(CHANGE_PASSWORD_URL_FORMAT, changePasswordConfig.getBaseUrl(),
                event.getTokenModel().getToken());
        Map<String, String> templateVariables = newHashMap();
        templateVariables.put(CHANGE_PASSWORD_URL_KEY, changePasswordUrl);
        templateVariables.put(VALIDITY_MINUTES_KEY, String.valueOf(changePasswordConfig.getValidityMinutes()));
        return templateVariables;
    }

    @Override
    String getReceiver(ChangePasswordNotificationEvent event) {
        UserEntity userEntity = userService.getById(event.getTokenModel().getUserId());
        return userEntity.getEmail();
    }
}
