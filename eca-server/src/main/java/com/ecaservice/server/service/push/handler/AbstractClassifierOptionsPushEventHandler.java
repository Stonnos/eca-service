package com.ecaservice.server.service.push.handler;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.event.model.push.AbstractClassifierOptionsPushEvent;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.service.classifiers.ClassifiersTemplateProvider;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;

import java.util.Map;

import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.CLASSIFIERS_CONFIGURATION_PARAM;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.CLASSIFIER_OPTIONS_DESCRIPTION;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.CLASSIFIER_OPTIONS_ID;

/**
 * Set active classifiers configuration push event handler.
 *
 * @author Roman Batygin
 */
public abstract class AbstractClassifierOptionsPushEventHandler<E extends AbstractClassifierOptionsPushEvent>
        extends AbstractChangeClassifiersConfigurationPushEventHandler<E> {

    private final ClassifiersTemplateProvider classifiersTemplateProvider;
    private final ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;

    /**
     * Constructor with parameters.
     *
     * @param clazz                                     - push event class
     * @param classifiersConfigurationHistoryRepository - classifiers configuration history repository
     * @param messageTemplateProcessor                  - message template processor
     * @param classifiersTemplateProvider               - classifiers template provider
     * @param classifierOptionsDatabaseModelRepository  - classifier options database model repository
     */
    protected AbstractClassifierOptionsPushEventHandler(
            Class<E> clazz,
            ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository,
            MessageTemplateProcessor messageTemplateProcessor,
            ClassifiersTemplateProvider classifiersTemplateProvider,
            ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository) {
        super(clazz, classifiersConfigurationHistoryRepository, messageTemplateProcessor);
        this.classifiersTemplateProvider = classifiersTemplateProvider;
        this.classifierOptionsDatabaseModelRepository = classifierOptionsDatabaseModelRepository;
    }

    @Override
    protected Map<String, Object> createMessageTemplateParams(AbstractClassifierOptionsPushEvent event) {
        long classifierOptionsId = event.getClassifierOptionsId();
        var classifierOptionsModel = classifierOptionsDatabaseModelRepository.findById(classifierOptionsId)
                .orElseThrow(
                        () -> new EntityNotFoundException(ClassifierOptionsDatabaseModel.class, classifierOptionsId));
        var classifierFormTemplate =
                classifiersTemplateProvider.getClassifierTemplateByClass(classifierOptionsModel.getOptionsName());
        return Map.of(
                CLASSIFIERS_CONFIGURATION_PARAM, event.getClassifiersConfiguration(),
                CLASSIFIER_OPTIONS_ID, classifierOptionsId,
                CLASSIFIER_OPTIONS_DESCRIPTION, classifierFormTemplate.getTemplateTitle()
        );
    }
}
