package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.event.model.TfaCodeEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

import static com.ecaservice.notification.util.Priority.HIGHEST;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.TFA_CODE;

/**
 * Tfa code email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class TfaCodeEmailEventHandler extends AbstractUserEmailEventHandler<TfaCodeEmailEvent> {

    /**
     * Creates tfa code notification event handler.
     */
    public TfaCodeEmailEventHandler() {
        super(TfaCodeEmailEvent.class);
    }

    @Override
    public Map<String, String> createVariables(TfaCodeEmailEvent event) {
        return Collections.singletonMap(TFA_CODE, event.getTfaCode());
    }

    @Override
    public String getTemplateCode(TfaCodeEmailEvent emailEvent) {
        return Templates.TFA_CODE;
    }

    @Override
    public int getPriority() {
        return HIGHEST;
    }
}
