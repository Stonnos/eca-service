package com.ecaservice.oauth.event.listener.handler;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.event.model.UserCreatedEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.PASSWORD_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.USERNAME_KEY;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements user created event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class UserCreatedEventHandler extends AbstractNotificationEventHandler<UserCreatedEvent> {

    /**
     * Creates user created event handler.
     */
    public UserCreatedEventHandler() {
        super(UserCreatedEvent.class, Templates.NEW_USER);
    }

    @Override
    Map<String, String> createVariables(UserCreatedEvent event) {
        UserEntity userEntity = event.getUserEntity();
        Map<String, String> templateVariables = newHashMap();
        templateVariables.put(USERNAME_KEY, userEntity.getLogin());
        templateVariables.put(PASSWORD_KEY, event.getPassword());
        return templateVariables;
    }

    @Override
    String getReceiver(UserCreatedEvent event) {
        return event.getUserEntity().getEmail();
    }
}
