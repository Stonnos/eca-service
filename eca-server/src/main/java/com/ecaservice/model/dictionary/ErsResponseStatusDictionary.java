package com.ecaservice.model.dictionary;

import lombok.experimental.UtilityClass;

/**
 * ERS response status dictionary.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ErsResponseStatusDictionary {

    public static final String SUCCESS_DESCRIPTION = "Успешно";
    public static final String INVALID_REQUEST_ID_DESCRIPTION = "Не задан UUID заявки";
    public static final String DUPLICATE_REQUEST_ID_DESCRIPTION = "Заявка с таким UUID уже существует";
    public static final String ERROR_DESCRIPTION = "Ошибка";
    public static final String INVALID_REQUEST_PARAMS_DESCRIPTION = "Не заданы обязательные параметры запроса";
    public static final String DATA_NOT_FOUND_DESCRIPTION = "Не найдена обучающая выборка";
    public static final String RESULTS_NOT_FOUND = "Не найдены оптимальные конфигурации моделей";
}
