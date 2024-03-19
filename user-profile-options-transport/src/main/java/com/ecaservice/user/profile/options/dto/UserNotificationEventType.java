package com.ecaservice.user.profile.options.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * User notification event type.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum UserNotificationEventType {

    /**
     * Experiment status change event
     */
    EXPERIMENT_STATUS_CHANGE("Изменение статуса заявки на эксперимент"),

    /**
     * Classifier model status change event
     */
    CLASSIFIER_STATUS_CHANGE("Изменение статуса построения модели классификатора"),

    /**
     * Experiment classifiers configuration data change event
     */
    CLASSIFIER_CONFIGURATION_CHANGE("Изменение данных конфигурации классификаторов для экспериментов");

    /**
     * Event description
     */
    private final String description;
}
