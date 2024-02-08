package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.event.model.ChangePasswordRequestEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.CONFIRMATION_CODE_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.VALIDITY_MINUTES_KEY;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements change password email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ChangePasswordRequestEmailEventHandler
        extends AbstractTokenEmailEventHandler<ChangePasswordRequestEmailEvent> {

    private final AppProperties appProperties;

    /**
     * Creates change password notification event handler.
     *
     * @param appProperties - app properties
     */
    public ChangePasswordRequestEmailEventHandler(AppProperties appProperties) {
        super(ChangePasswordRequestEmailEvent.class);
        this.appProperties = appProperties;
    }

    @Override
    public String getTemplateCode(ChangePasswordRequestEmailEvent emailEvent) {
        return Templates.CHANGE_PASSWORD;
    }

    @Override
    public Map<String, String> createVariables(ChangePasswordRequestEmailEvent event) {
        Map<String, String> templateVariables = newHashMap();
        templateVariables.put(CONFIRMATION_CODE_KEY, event.getTokenModel().getConfirmationCode());
        templateVariables.put(VALIDITY_MINUTES_KEY,
                String.valueOf(appProperties.getChangePassword().getValidityMinutes()));
        return templateVariables;
    }
}
