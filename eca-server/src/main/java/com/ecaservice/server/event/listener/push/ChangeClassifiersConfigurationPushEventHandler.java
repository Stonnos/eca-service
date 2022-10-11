package com.ecaservice.server.event.listener.push;

import com.ecaservice.server.event.model.push.ChangeClassifiersConfigurationPushEvent;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ecaservice.server.service.push.dictionary.PushProperties.CLASSIFIERS_CONFIGURATION_ID_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.CLASSIFIER_CONFIGURATION_CHANGE_MESSAGE_TYPE;

/**
 * Change classifiers configuration push event handler.
 *
 * @param <E> - push event generic type
 * @author Roman Batygin
 */
@Slf4j
public abstract class ChangeClassifiersConfigurationPushEventHandler<E extends ChangeClassifiersConfigurationPushEvent>
        extends AbstractUserPushNotificationEventHandler<E> {

    private final ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository;

    /**
     * Constructor with parameters.
     *
     * @param clazz                                     - push event class
     * @param classifiersConfigurationHistoryRepository - classifiers configuration history repository
     */
    protected ChangeClassifiersConfigurationPushEventHandler(Class<E> clazz,
                                                             ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository) {
        super(clazz);
        this.classifiersConfigurationHistoryRepository = classifiersConfigurationHistoryRepository;
    }

    @Override
    protected List<String> getReceivers(ChangeClassifiersConfigurationPushEvent event) {
        var classifiersConfiguration = event.getClassifiersConfiguration();
        log.info("Starting to get receivers for classifiers configuration [{}] event [{}]",
                classifiersConfiguration.getId(), event.getClass().getSimpleName());
        var allModifiers = classifiersConfigurationHistoryRepository.getAllModifiers(classifiersConfiguration)
                .stream()
                .filter(user -> !Objects.equals(event.getInitiator(), user))
                .collect(Collectors.toList());
        log.info("[{}] receivers has been fetched for classifiers configuration [{}] event [{}]", allModifiers.size(),
                classifiersConfiguration.getId(), event.getClass().getSimpleName());
        log.info("Classifiers configuration [{}] event [{}] receivers: {}", classifiersConfiguration.getId(),
                event.getClass().getSimpleName(), allModifiers);
        return allModifiers;
    }

    @Override
    protected String getMessageType() {
        return CLASSIFIER_CONFIGURATION_CHANGE_MESSAGE_TYPE;
    }

    @Override
    protected String getMessageText(E event) {
        return null;
    }

    @Override
    protected Map<String, String> createAdditionalProperties(E event) {
        return Collections.singletonMap(CLASSIFIERS_CONFIGURATION_ID_PROPERTY,
                String.valueOf(event.getClassifiersConfiguration().getId()));
    }
}
