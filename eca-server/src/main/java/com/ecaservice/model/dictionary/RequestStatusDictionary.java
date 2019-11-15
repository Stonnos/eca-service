package com.ecaservice.model.dictionary;

import lombok.experimental.UtilityClass;

/**
 * Request status dictionary.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class RequestStatusDictionary {

    public static final String NEW_STATUS_DESCRIPTION = "Новая";
    public static final String FINISHED_STATUS_DESCRIPTION = "Завершена";
    public static final String ERROR_STATUS_DESCRIPTION = "Ошибка";
    public static final String TIMEOUT_STATUS_DESCRIPTION = "Таймаут";
}
