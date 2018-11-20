package com.ecaservice.web.dto;

/**
 * ERS response status enum.
 *
 * @author Roman batygin
 */
public enum ErsResponseStatus {

    SUCCESS("Успешно"),
    INVALID_REQUEST_ID("Не задан UUID заявки"),
    DUPLICATE_REQUEST_ID("Заявка с таким UUID уже существует"),
    ERROR("Ошибка"),
    INVALID_REQUEST_PARAMS("Не заданы обязательные параметры в запросе"),
    DATA_NOT_FOUND("Не найдена обучающая выборка"),
    RESULTS_NOT_FOUND("Не найдены оптимальные конфигурации моделей");

    private String description;

    ErsResponseStatus(String description) {
        this.description = description;
    }

    /**
     * ERS response status description.
     *
     * @return ERS response status status description
     */
    public String getDescription() {
        return description;
    }
}
