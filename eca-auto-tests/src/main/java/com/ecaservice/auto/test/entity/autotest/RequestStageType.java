package com.ecaservice.auto.test.entity.autotest;

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
     * Ready request
     */
    READY("Запрос готов к отправке"),

    /**
     * Request sent
     */
    REQUEST_SENT("Запрос отправлен"),

    /**
     * Request created
     */
    REQUEST_CREATED("Заявка успешно создана"),

    /**
     * Request finished
     */
    REQUEST_FINISHED("Заявка успешно завершена"),

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
