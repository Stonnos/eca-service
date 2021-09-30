package com.ecaservice.server.model.dictionary;

import lombok.experimental.UtilityClass;

/**
 * ERS response status dictionary.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ErsResponseStatusDictionary {

    public static final String SUCCESS_DESCRIPTION = "Успешно";
    public static final String DUPLICATE_REQUEST_ID_DESCRIPTION = "Заявка с таким UUID уже существует";
    public static final String ERROR_DESCRIPTION = "Ошибка";
    public static final String DATA_NOT_FOUND_DESCRIPTION = "Не найдена обучающая выборка";
    public static final String RESULTS_NOT_FOUND_DESCRIPTION = "Не найдены оптимальные конфигурации моделей";
    public static final String SERVICE_UNAVAILABLE_DESCRIPTION = "Сервис не доступен";
}
