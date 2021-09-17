package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.event.model.EmailChangedNotificationEvent;
import com.ecaservice.oauth.event.model.TfaCodeNotificationEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

import static com.ecaservice.notification.util.Priority.HIGHEST;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.TFA_CODE;

/**
 * Email changed notification event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EmailChangedNotificationEventHandler extends AbstractNotificationEventHandler<EmailChangedNotificationEvent> {

    /**
     * Creates email changed notification event handler.
     */
    public EmailChangedNotificationEventHandler() {
        super(EmailChangedNotificationEvent.class, Templates.EMAIL_CHANGED);
    }

    @Override
    Map<String, String> createVariables(EmailChangedNotificationEvent event) {
        return Collections.emptyMap();
    }

    @Override
    String getReceiver(EmailChangedNotificationEvent event) {
        return event.getUserEntity().getEmail();
    }
}
