package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.ForceSetPasswordRequestConfirmationCodeEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.CONFIRMATION_CODE_KEY;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements force set password confirmation code email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ForceSetPasswordConfirmationCodeEmailEventHandler
        extends AbstractTokenEmailEventHandler<ForceSetPasswordRequestConfirmationCodeEmailEvent> {

    /**
     * Creates set password confirmation code email notification event handler.
     *
     */
    public ForceSetPasswordConfirmationCodeEmailEventHandler() {
        super(ForceSetPasswordRequestConfirmationCodeEmailEvent.class);
    }

    @Override
    public String getTemplateCode(ForceSetPasswordRequestConfirmationCodeEmailEvent emailEvent) {
        return Templates.SET_PASSWORD_CONFIRMATION_CODE;
    }

    @Override
    public Map<String, String> createVariables(ForceSetPasswordRequestConfirmationCodeEmailEvent event) {
        Map<String, String> templateVariables = newHashMap();
        templateVariables.put(CONFIRMATION_CODE_KEY, event.getTokenModel().getConfirmationCode());
        return templateVariables;
    }
}
