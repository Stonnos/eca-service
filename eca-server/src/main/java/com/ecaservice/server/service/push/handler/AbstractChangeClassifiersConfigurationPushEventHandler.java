package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.AbstractChangeClassifiersConfigurationPushEvent;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ecaservice.server.service.push.dictionary.PushProperties.CLASSIFIERS_CONFIGURATION_ID_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.CLASSIFIER_CONFIGURATION_CHANGE_MESSAGE_TYPE;

/**
 * Change classifiers configuration push event handler.
 *
 * @param <E> - push event generic type
 * @author Roman Batygin
 */
@Slf4j
public abstract class AbstractChangeClassifiersConfigurationPushEventHandler<E extends AbstractChangeClassifiersConfigurationPushEvent>
        extends AbstractUserPushNotificationEventHandler<E> {

    private final ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository;
    private final MessageTemplateProcessor messageTemplateProcessor;

    /**
     * Constructor with parameters.
     *
     * @param clazz                                     - push event class
     * @param classifiersConfigurationHistoryRepository - classifiers configuration history repository
     * @param messageTemplateProcessor                  - message template processor
     */
    protected AbstractChangeClassifiersConfigurationPushEventHandler(
            Class<E> clazz,
            ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository,
            MessageTemplateProcessor messageTemplateProcessor) {
        super(clazz);
        this.classifiersConfigurationHistoryRepository = classifiersConfigurationHistoryRepository;
        this.messageTemplateProcessor = messageTemplateProcessor;
    }

    @Override
    public boolean isValid(AbstractChangeClassifiersConfigurationPushEvent event) {
        long modificationsCount = classifiersConfigurationHistoryRepository.getAnotherUsersModificationsCount(
                event.getClassifiersConfiguration(), event.getInitiator());
        if (modificationsCount == 0L) {
            log.warn("No one another modifier found for classifier configuration [{}]",
                    event.getClassifiersConfiguration().getId());
            return false;
        }
        return true;
    }

    @Override
    protected List<String> getReceivers(AbstractChangeClassifiersConfigurationPushEvent event) {
        var classifiersConfiguration = event.getClassifiersConfiguration();
        log.info("Starting to get receivers for classifiers configuration [{}] event [{}]",
                classifiersConfiguration.getId(), event.getClass().getSimpleName());
        var allModifiers = classifiersConfigurationHistoryRepository.getAnotherModifiers(classifiersConfiguration,
                event.getInitiator());
        log.info("[{}] receivers has been fetched for classifiers configuration [{}] event [{}]", allModifiers.size(),
                classifiersConfiguration.getId(), event.getClass().getSimpleName());
        log.info("Classifiers configuration [{}] event [{}] receivers: {}", classifiersConfiguration.getId(),
                event.getClass().getSimpleName(), allModifiers);
        return allModifiers;
    }

    @Override
    protected String getMessageType(AbstractChangeClassifiersConfigurationPushEvent event) {
        return CLASSIFIER_CONFIGURATION_CHANGE_MESSAGE_TYPE;
    }

    @Override
    protected String getMessageText(E event) {
        var templateParams = createMessageTemplateParams(event);
        return messageTemplateProcessor.process(getMessageTemplateCode(), templateParams);
    }

    @Override
    protected Map<String, String> createAdditionalProperties(E event) {
        return Collections.singletonMap(CLASSIFIERS_CONFIGURATION_ID_PROPERTY,
                String.valueOf(event.getClassifiersConfiguration().getId()));
    }

    /**
     * Gets push message template code.
     *
     * @return message template code
     */
    protected abstract String getMessageTemplateCode();

    /**
     * Creates push message template params.
     *
     * @param event - push event
     * @return message template params
     */
    protected abstract Map<String, Object> createMessageTemplateParams(E event);
}
