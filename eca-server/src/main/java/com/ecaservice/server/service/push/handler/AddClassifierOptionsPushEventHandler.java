package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.AddClassifierOptionsPushEvent;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.service.classifiers.ClassifiersFormTemplateProvider;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.ADD_CLASSIFIERS_CONFIGURATION_OPTIONS_PUSH_MESSAGE;

/**
 * Add classifier options push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class AddClassifierOptionsPushEventHandler
        extends AbstractClassifierOptionsPushEventHandler<AddClassifierOptionsPushEvent> {

    /**
     * Constructor with parameters.
     *
     * @param classifiersConfigurationHistoryRepository - classifiers configuration history repository
     * @param messageTemplateProcessor                  - message template processor
     * @param classifiersFormTemplateProvider           - classifiers template provider
     */
    public AddClassifierOptionsPushEventHandler(
            ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository,
            MessageTemplateProcessor messageTemplateProcessor,
            ClassifiersFormTemplateProvider classifiersFormTemplateProvider) {
        super(AddClassifierOptionsPushEvent.class, classifiersConfigurationHistoryRepository, messageTemplateProcessor,
                classifiersFormTemplateProvider);
    }

    @Override
    protected String getMessageTemplateCode() {
        return ADD_CLASSIFIERS_CONFIGURATION_OPTIONS_PUSH_MESSAGE;
    }
}
