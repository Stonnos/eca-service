package com.ecaservice.server.event.listener.push;

import com.ecaservice.server.event.model.push.AddClassifierOptionsPushEvent;
import com.ecaservice.server.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.service.classifiers.ClassifiersTemplateProvider;
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
     * @param classifiersTemplateProvider               - classifiers template provider
     * @param classifierOptionsDatabaseModelRepository  - classifier options database model repository
     */
    public AddClassifierOptionsPushEventHandler(
            ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository,
            MessageTemplateProcessor messageTemplateProcessor,
            ClassifiersTemplateProvider classifiersTemplateProvider,
            ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository) {
        super(AddClassifierOptionsPushEvent.class, classifiersConfigurationHistoryRepository, messageTemplateProcessor,
                classifiersTemplateProvider, classifierOptionsDatabaseModelRepository);
    }

    @Override
    protected String getMessageTemplateCode() {
        return ADD_CLASSIFIERS_CONFIGURATION_OPTIONS_PUSH_MESSAGE;
    }
}
