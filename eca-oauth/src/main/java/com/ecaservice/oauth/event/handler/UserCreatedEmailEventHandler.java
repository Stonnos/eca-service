package com.ecaservice.oauth.event.handler;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.event.model.UserCreatedEmailEvent;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.PASSWORD_KEY;
import static com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary.USERNAME_KEY;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements user created email event handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class UserCreatedEmailEventHandler extends AbstractUserEmailEventHandler<UserCreatedEmailEvent> {

    /**
     * Creates user created event handler.
     */
    public UserCreatedEmailEventHandler() {
        super(UserCreatedEmailEvent.class);
    }

    @Override
    public String getTemplateCode(UserCreatedEmailEvent emailEvent) {
        return Templates.NEW_USER;
    }

    @Override
    public Map<String, String> createVariables(UserCreatedEmailEvent event) {
        UserEntity userEntity = event.getUserEntity();
        Map<String, String> templateVariables = newHashMap();
        templateVariables.put(USERNAME_KEY, userEntity.getLogin());
        templateVariables.put(PASSWORD_KEY, event.getPassword());
        return templateVariables;
    }
}
