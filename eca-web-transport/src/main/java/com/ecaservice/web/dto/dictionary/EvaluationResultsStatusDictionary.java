package com.ecaservice.web.dto.dictionary;

import lombok.experimental.UtilityClass;

/**
 * Evaluation results status status dictionary.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class EvaluationResultsStatusDictionary {

    public static final String RESULTS_RECEIVED_DESCRIPTION = "Получены результаты классификации";
    public static final String EVALUATION_IN_PROGRESS_DESCRIPTION = "Идет построение модели классификатора...";
    public static final String EVALUATION_ERROR_DESCRIPTION =
            "Невозможно получить результаты классификации, т.к. произошла ошибка при построении модели классификатора";
    public static final String RESULTS_NOT_SENT_DESCRIPTION =
            "Не удалось получить результаты классификации, т.к. они не были отправлены в ERS сервис";
    public static final String EVALUATION_RESULTS_NOT_FOUND_DESCRIPTION =
            "Результаты классификации не были найдены в ERS";
    public static final String ERROR_DESCRIPTION =
            "Не удалось получить результаты классификации, т.к. произошла неизвестная ошибка";
    public static final String ERS_SERVICE_UNAVAILABLE_DESCRIPTION =
            "Не удалось получить результаты классификации, т.к. ERS сервис не доступен";
}
