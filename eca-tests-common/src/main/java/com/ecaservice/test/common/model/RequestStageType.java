package com.ecaservice.test.common.model;

import eca.core.DescriptiveEnum;
import lombok.RequiredArgsConstructor;

/**
 * Request stage type.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum RequestStageType implements DescriptiveEnum {

    /**
     * Request sent
     */
    REQUEST_SENT("Запрос отправлен"),

    /**
     * Response received
     */
    RESPONSE_RECEIVED("Получен ответ"),

    /**
     * Error status
     */
    ERROR("Ошибка"),

    /**
     * Request not send
     */
    NOT_SEND("Запрос не был отправлен"),

    /**
     * Request exceeded
     */
    EXCEEDED("Истелко время ожидания ответа"),

    /**
     * Request processing completed
     */
    COMPLETED("Завершен");

    private final String description;

    @Override
    public String getDescription() {
        return description;
    }
}
