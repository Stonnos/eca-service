package com.ecaservice.server.model.entity;

import eca.core.DescriptiveEnum;
import lombok.RequiredArgsConstructor;

/**
 * Classifiers configuration action type.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum ClassifiersConfigurationActionType implements DescriptiveEnum {

    /**
     * Create classifiers configuration
     */
    CREATE_CONFIGURATION("Создана новая конфигурация"),

    /**
     * Update classifiers configuration
     */
    UPDATE_CONFIGURATION("Данные конфигурации изменены"),

    /**
     * Set active configuration
     */
    SET_ACTIVE("Конфигурация сделана активной"),

    /**
     * Add classifier options
     */
    ADD_CLASSIFIER_OPTIONS("Добавлены новые настройки классификатора"),

    /**
     * Remove classifier options
     */
    REMOVE_CLASSIFIER_OPTIONS("Удалены настройки классификатора");

    private final String description;

    /**
     * Gets action type description.
     *
     * @return action type description
     */
    @Override
    public String getDescription() {
        return description;
    }
}
