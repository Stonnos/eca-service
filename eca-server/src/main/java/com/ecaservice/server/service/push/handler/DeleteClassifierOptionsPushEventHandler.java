package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.DeleteClassifierOptionsPushEvent;
import com.ecaservice.server.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.service.classifiers.ClassifiersTemplateProvider;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.DELETE_CLASSIFIERS_CONFIGURATION_OPTIONS_PUSH_MESSAGE;

/**
 * Delete classifier options push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class DeleteClassifierOptionsPushEventHandler
        extends AbstractClassifierOptionsPushEventHandler<DeleteClassifierOptionsPushEvent> {

    /**
     * Constructor with parameters.
     *
     * @param classifiersConfigurationHistoryRepository - classifiers configuration history repository
     * @param messageTemplateProcessor                  - message template processor
     * @param classifiersTemplateProvider               - classifiers template provider
     * @param classifierOptionsDatabaseModelRepository  - classifier options database model repository
     */
    public DeleteClassifierOptionsPushEventHandler(
            ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository,
            MessageTemplateProcessor messageTemplateProcessor,
            ClassifiersTemplateProvider classifiersTemplateProvider,
            ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository) {
        super(DeleteClassifierOptionsPushEvent.class, classifiersConfigurationHistoryRepository,
                messageTemplateProcessor, classifiersTemplateProvider, classifierOptionsDatabaseModelRepository);
    }

    @Override
    protected String getMessageTemplateCode() {
        return DELETE_CLASSIFIERS_CONFIGURATION_OPTIONS_PUSH_MESSAGE;
    }
}
