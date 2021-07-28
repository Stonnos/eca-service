package com.ecaservice.auto.test.entity;

import eca.core.DescriptiveEnum;
import lombok.RequiredArgsConstructor;

/**
 * Request stage type.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum ExperimentRequestStageType implements DescriptiveEnum {

    /**
     * Ready request
     */
    READY("Запрос готов к отправке"),

    /**
     * Request sent
     */
    REQUEST_SENT("Запрос отправлен"),

    /**
     * Response received from mq
     */
    REQUEST_CREATED("Заявка на эксперимент успешно создана"),

    /**
     * Request finished
     */
    REQUEST_FINISHED("Эксперимент успешно завершен"),

    /**
     * Request processing completed
     */
    COMPLETED("Результаты эксперимента успешно обработаны"),

    /**
     * Request exceeded
     */
    EXCEEDED("Истелко время ожидания обработки заявки"),

    /**
     * Error status
     */
    ERROR("Неизвестаная ошибка");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
