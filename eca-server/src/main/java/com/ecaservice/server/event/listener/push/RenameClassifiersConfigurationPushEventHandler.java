package com.ecaservice.server.event.listener.push;

import com.ecaservice.server.event.model.push.RenameClassifiersConfigurationPushEvent;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
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
        extends ChangeClassifiersConfigurationPushEventHandler<RenameClassifiersConfigurationPushEvent> {

    /**
     * Constructor with parameters.
     *
     * @param classifiersConfigurationHistoryRepository - classifiers configuration history repository
     * @param messageTemplateProcessor                  - message template processor
     */
    public RenameClassifiersConfigurationPushEventHandler(
            ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository,
            MessageTemplateProcessor messageTemplateProcessor) {
        super(RenameClassifiersConfigurationPushEvent.class, classifiersConfigurationHistoryRepository,
                messageTemplateProcessor);
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
