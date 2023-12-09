package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.AbstractClassifierOptionsPushEvent;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.service.classifiers.ClassifiersFormTemplateProvider;
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

    private final ClassifiersFormTemplateProvider classifiersFormTemplateProvider;

    /**
     * Constructor with parameters.
     *
     * @param clazz                                     - push event class
     * @param classifiersConfigurationHistoryRepository - classifiers configuration history repository
     * @param messageTemplateProcessor                  - message template processor
     * @param classifiersFormTemplateProvider           - classifiers template provider
     */
    protected AbstractClassifierOptionsPushEventHandler(
            Class<E> clazz,
            ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository,
            MessageTemplateProcessor messageTemplateProcessor,
            ClassifiersFormTemplateProvider classifiersFormTemplateProvider) {
        super(clazz, classifiersConfigurationHistoryRepository, messageTemplateProcessor);
        this.classifiersFormTemplateProvider = classifiersFormTemplateProvider;
    }

    @Override
    protected Map<String, Object> createMessageTemplateParams(AbstractClassifierOptionsPushEvent event) {
        long classifierOptionsId = event.getClassifierOptionsId();
        var classifierFormTemplate =
                classifiersFormTemplateProvider.getClassifierTemplateByClass(event.getOptionsName());
        return Map.of(
                CLASSIFIERS_CONFIGURATION_PARAM, event.getClassifiersConfiguration(),
                CLASSIFIER_OPTIONS_ID, classifierOptionsId,
                CLASSIFIER_OPTIONS_DESCRIPTION, classifierFormTemplate.getTemplateTitle()
        );
    }
}
