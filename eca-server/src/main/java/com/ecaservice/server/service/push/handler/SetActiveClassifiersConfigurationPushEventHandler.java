package com.ecaservice.server.service.push.handler;

import com.ecaservice.core.message.template.service.MessageTemplateProcessor;
import com.ecaservice.server.event.model.push.SetActiveClassifiersConfigurationPushEvent;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.user.profile.options.client.service.UserProfileOptionsProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.SET_ACTIVE_CLASSIFIERS_CONFIGURATION_PUSH_MESSAGE;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.CLASSIFIERS_CONFIGURATION_PARAM;

/**
 * Set active classifiers configuration push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class SetActiveClassifiersConfigurationPushEventHandler
        extends AbstractChangeClassifiersConfigurationPushEventHandler<SetActiveClassifiersConfigurationPushEvent> {

    /**
     * Constructor with parameters.
     *
     * @param classifiersConfigurationHistoryRepository - classifiers configuration history repository
     * @param messageTemplateProcessor                  - message template processor
     * @param userProfileOptionsProvider                - user profile options provider
     */
    public SetActiveClassifiersConfigurationPushEventHandler(
            ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository,
            MessageTemplateProcessor messageTemplateProcessor,
            UserProfileOptionsProvider userProfileOptionsProvider) {
        super(SetActiveClassifiersConfigurationPushEvent.class, classifiersConfigurationHistoryRepository,
                messageTemplateProcessor, userProfileOptionsProvider);
    }

    @Override
    protected String getMessageTemplateCode() {
        return SET_ACTIVE_CLASSIFIERS_CONFIGURATION_PUSH_MESSAGE;
    }

    @Override
    protected Map<String, Object> createMessageTemplateParams(SetActiveClassifiersConfigurationPushEvent event) {
        return Collections.singletonMap(CLASSIFIERS_CONFIGURATION_PARAM, event.getClassifiersConfiguration());
    }
}
