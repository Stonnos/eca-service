package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.event.model.TfaCodeNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

import static com.ecaservice.notification.util.Priority.HIGHEST;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.TFA_CODE;

/**
 * Tfa code notification event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class TfaCodeNotificationEventHandler extends AbstractNotificationEventHandler<TfaCodeNotificationEvent> {

    /**
     * Creates tfa code notification event handler.
     */
    public TfaCodeNotificationEventHandler() {
        super(TfaCodeNotificationEvent.class, Templates.TFA_CODE);
    }

    @Override
    Map<String, String> createVariables(TfaCodeNotificationEvent event) {
        return Collections.singletonMap(TFA_CODE, event.getTfaCode());
    }

    @Override
    int getPriority() {
        return HIGHEST;
    }
}
