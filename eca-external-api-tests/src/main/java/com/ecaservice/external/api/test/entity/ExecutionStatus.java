package com.ecaservice.external.api.test.entity;

import eca.core.DescriptiveEnum;
import lombok.RequiredArgsConstructor;

/**
 * Test status.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum ExecutionStatus implements DescriptiveEnum {

    /**
     * New status
     */
    NEW("Новый"),

    /**
     * Test in progress
     */
    IN_PROGRESS("Выполняется"),

    /**
     * Tests finished status
     */
    FINISHED("Завершен"),

    /**
     * Timeout status
     */
    TIMEOUT("Таймаут"),

    /**
     * Unknown error
     */
    ERROR("Ошибка");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
