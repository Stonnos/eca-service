package com.ecaservice.web.dto.dictionary;

import lombok.experimental.UtilityClass;

/**
 * ERS report status dictionary.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ErsReportStatusDictionary {

    public static final String FETCHED_DESCRIPTION =
            "Результаты эксперимента успешно получены";
    public static final String NEW_EXPERIMENT_DESCRIPTION = "Эксперимент находится в очереди на обработку...";
    public static final String EXPERIMENT_IN_PROGRESS_DESCRIPTION = "Идет построение эксперимента...";
    public static final String EXPERIMENT_ERROR_DESCRIPTION =
            "Невозможно получить результаты эксперимента. В ходе построения эксперимента возникла ошибка";

    public static final String EXPERIMENT_CANCEL_DESCRIPTION =
            "Невозможно получить результаты эксперимента. Построение эксперимента прервано";
    public static final String EXPERIMENT_RESULTS_NOT_FOUND_DESCRIPTION = "Результаты эксперимента не найдены";
}
