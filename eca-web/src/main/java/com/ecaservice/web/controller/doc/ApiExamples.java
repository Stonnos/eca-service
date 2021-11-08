package com.ecaservice.web.controller.doc;

import lombok.experimental.UtilityClass;

/**
 * Api examples utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ApiExamples {

    /**
     * Menu bar response json
     */
    public static final String MENU_BAR_RESPONSE_JSON = "[{\"label\": \"Эксперименты\", \"routerLink\": " +
            "\"/dashboard/experiments\", \"items\": null}, {\"label\": \"Классификаторы\", " +
            "\"routerLink\": \"/dashboard/classifiers\", \"items\": null}, {\"label\": " +
            "\"Оптимальные настройки классификаторов\", \"routerLink\": \"/dashboard/classifiers-options-requests\", " +
            "\"items\": null}, {\"label\": \"Датасеты\", \"routerLink\": \"/dashboard/instances\", " +
            "\"items\": null}, {\"label\": \"Пользователи\", \"routerLink\": \"/dashboard/users\", " +
            "\"items\": null}, {\"label\": \"Шаблоны email сообщений\", \"routerLink\": " +
            "\"/dashboard/email-templates\", \"items\": null}, {\"label\": \"Журнал аудита\", " +
            "\"routerLink\": \"/dashboard/audit-logs\", \"items\": null}]";
}
