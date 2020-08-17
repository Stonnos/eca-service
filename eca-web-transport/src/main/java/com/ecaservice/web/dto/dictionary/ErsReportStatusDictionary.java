package com.ecaservice.web.dto.dictionary;

import lombok.experimental.UtilityClass;

/**
 * ERS report status dictionary.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ErsReportStatusDictionary {

    public static final String SUCCESS_SENT_DESCRIPTION =
            "Результаты эксперимента были успешно отправлены в ERS сервис";
    public static final String NEW_EXPERIMENT_DESCRIPTION = "Эксперимент находится в очереди на обработку";
    public static final String EXPERIMENT_IN_PROGRESS_DESCRIPTION = "Идет построение эксперимента...";
    public static final String EXPERIMENT_ERROR_DESCRIPTION =
            "Невозможно отправить результаты эксперимента в ERS сервис. В ходе построения эксперимента возникла ошибка";
    public static final String EXPERIMENT_RESULTS_NOT_FOUND_DESCRIPTION =
            "Результаты эксперимента для отправки в ERS сервис не найдены";
    public static final String EXPERIMENT_DELETED_DESCRIPTION =
            "Невозможно отправить результаты эксперимента в ERS сервис. Файл с результатами эксперимента был удален";
    public static final String NOT_SENT_DESCRIPTION =
            "Некоторые результаты эксперимента не были отправлены в ERS сервис";
}
