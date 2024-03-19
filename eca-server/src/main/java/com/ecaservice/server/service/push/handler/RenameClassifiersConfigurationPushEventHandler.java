package com.ecaservice.server.service.push.handler;

import com.ecaservice.core.message.template.service.MessageTemplateProcessor;
import com.ecaservice.server.event.model.push.RenameClassifiersConfigurationPushEvent;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.user.profile.options.client.service.UserProfileOptionsProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.RENAME_CLASSIFIERS_CONFIGURATION_PUSH_MESSAGE;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.CLASSIFIERS_CONFIGURATION_PARAM;

/**
 * Rename classifiers configuration push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class RenameClassifiersConfigurationPushEventHandler
        extends AbstractChangeClassifiersConfigurationPushEventHandler<RenameClassifiersConfigurationPushEvent> {

    /**
     * Constructor with parameters.
     *
     * @param classifiersConfigurationHistoryRepository - classifiers configuration history repository
     * @param messageTemplateProcessor                  - message template processor
     * @param userProfileOptionsProvider                - user profile options provider
     */
    public RenameClassifiersConfigurationPushEventHandler(
            ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository,
            MessageTemplateProcessor messageTemplateProcessor,
            UserProfileOptionsProvider userProfileOptionsProvider) {
        super(RenameClassifiersConfigurationPushEvent.class, classifiersConfigurationHistoryRepository,
                messageTemplateProcessor, userProfileOptionsProvider);
    }

    @Override
    protected String getMessageTemplateCode() {
        return RENAME_CLASSIFIERS_CONFIGURATION_PUSH_MESSAGE;
    }

    @Override
    protected Map<String, Object> createMessageTemplateParams(RenameClassifiersConfigurationPushEvent event) {
        return Collections.singletonMap(CLASSIFIERS_CONFIGURATION_PARAM, event.getClassifiersConfiguration());
    }
}
