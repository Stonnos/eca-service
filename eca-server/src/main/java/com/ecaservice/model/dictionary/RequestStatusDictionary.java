package com.ecaservice.model.dictionary;

/**
 * Request status dictionary.
 *
 * @author Roman Batygin
 */
public class RequestStatusDictionary {

    private RequestStatusDictionary() {
    }

    public static final String NEW_STATUS_DESCRIPTION = "Новая";
    public static final String FINISHED_STATUS_DESCRIPTION = "Завершена";
    public static final String ERROR_STATUS_DESCRIPTION = "Ошибка";
    public static final String TIMEOUT_STATUS_DESCRIPTION = "Таймаут";
}
