package com.ecaservice.server.event.listener.push;

import com.ecaservice.server.event.model.push.AddClassifiersConfigurationOptionsPushEvent;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.ADD_CLASSIFIERS_CONFIGURATION_OPTIONS_PUSH_MESSAGE;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.CLASSIFIERS_CONFIGURATION_PARAM;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.CLASSIFIER_OPTIONS_DESCRIPTION;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.CLASSIFIER_OPTIONS_ID;

/**
 * Set active classifiers configuration push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class AddClassifiersConfigurationOptionsPushEventHandler
        extends ChangeClassifiersConfigurationPushEventHandler<AddClassifiersConfigurationOptionsPushEvent> {

    /**
     * Constructor with parameters.
     *
     * @param classifiersConfigurationHistoryRepository - classifiers configuration history repository
     * @param messageTemplateProcessor                  - message template processor
     */
    public AddClassifiersConfigurationOptionsPushEventHandler(
            ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository,
            MessageTemplateProcessor messageTemplateProcessor) {
        super(AddClassifiersConfigurationOptionsPushEvent.class, classifiersConfigurationHistoryRepository,
                messageTemplateProcessor);
    }

    @Override
    protected String getMessageTemplateCode() {
        return ADD_CLASSIFIERS_CONFIGURATION_OPTIONS_PUSH_MESSAGE;
    }

    @Override
    protected Map<String, Object> createMessageTemplateParams(AddClassifiersConfigurationOptionsPushEvent event) {
        return Map.of(
                CLASSIFIERS_CONFIGURATION_PARAM, event.getClassifiersConfiguration(),
                CLASSIFIER_OPTIONS_ID, event.getClassifierOptionsDto().getId(),
                CLASSIFIER_OPTIONS_DESCRIPTION, event.getClassifierOptionsDto().getOptionsDescription()
        );
    }
}
