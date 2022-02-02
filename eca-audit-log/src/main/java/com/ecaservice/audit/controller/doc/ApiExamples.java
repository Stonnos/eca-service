package com.ecaservice.audit.controller.doc;

import lombok.experimental.UtilityClass;

/**
 * Api examples utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ApiExamples {

    /**
     * Audit logs page request json
     */
    public static final String AUDIT_LOGS_PAGE_REQUEST_JSON =
            "{\"content\": [{\"eventId\": \"1d2de514-3a87-4620-9b97-c260e24340de\", " +
                    "\"correlationId\": \"202786\", \"message\": \"Some action\", " +
                    "\"initiator\": \"user\", \"groupCode\": \"USER_ACTIONS\", \"groupTitle\": " +
                    "\"User actions\", \"code\": \"LOGIN\", \"codeTitle\": \"User logged in\", " +
                    "\"eventDate\": \"2021-07-01 14:00:00\"}], \"page\": 0, \"totalCount\": 1}";

    /**
     * Audit event request json
     */
    public static final String AUDIT_EVENT_REQUEST_JSON = "{\"eventId\": \"a01ebc99-9c0b-4ef8-bb6d-6bb9bd380a11\", " +
            "\"correlationId\": \"202786\", " +
            "\"message\": \"Audit message\", \"initiator\": \"user\", \"eventType\": \"START\"," +
            " \"groupCode\": \"GROUP_CODE\", \"groupTitle\": \"\", \"code\": \"AUDIT_CODE\"," +
            " \"codeTitle\": \"\", \"eventDate\": \"2021-07-16 07:57:11\"}";

    /**
     * Audit filter response json
     */
    public static final String AUDIT_LOG_FILTER_RESPONSE_JSON = "[{\"fieldName\": \"eventId\", \"description\": \"ID " +
            "события\", \"fieldOrder\": 0, \"filterFieldType\": \"TEXT\", \"matchMode\": \"LIKE\", " +
            "\"multiple\": false, \"dictionary\": null}, {\"fieldName\": \"groupCode\", \"description\": " +
            "\"Группа событий\", \"fieldOrder\": 1, \"filterFieldType\": \"REFERENCE\", \"matchMode\": " +
            "\"EQUALS\", \"multiple\": false, \"dictionary\": {\"name\": \"auditGroup\", \"values\": " +
            "[{\"label\": \"Действия пользователя в личном кабинете\", \"value\": \"USER_PROFILE_ACTIONS\"}, " +
            "{\"label\": \"Действия с конфигурациями классификаторов\", \"value\": " +
            "\"CLASSIFIERS_CONFIGURATIONS_ACTIONS\"}, {\"label\": \"Действия с обучающими выборками\", " +
            "\"value\": \"DATA_STORAGE_ACTIONS\"}]}}, {\"fieldName\": \"initiator\", \"description\": " +
            "\"Инициатор события\", \"fieldOrder\": 2, \"filterFieldType\": \"TEXT\", \"matchMode\": " +
            "\"LIKE\", \"multiple\": false, \"dictionary\": null}, {\"fieldName\": \"eventDate\", " +
            "\"description\": \"Дата события\", \"fieldOrder\": 3, \"filterFieldType\": \"DATE\", " +
            "\"matchMode\": \"RANGE\", \"multiple\": true, \"dictionary\": null}]";

    /**
     * Audit logs page response json
     */
    public static final String AUDIT_LOGS_PAGE_RESPONSE_JSON = "{\"content\": [{\"eventId\": " +
            "\"1d2de514-3a87-4620-9b97-c260e24340de\", \"message\": \"Some action\", \"initiator\": \"user\", " +
            "\"groupCode\": \"USER_ACTIONS\", \"groupTitle\": \"User actions\", \"code\": \"LOGIN\", \"codeTitle\": " +
            "\"User logged in\", \"eventDate\": \"2021-07-01 14:00:00\"}], \"page\": 0, \"totalCount\": 1}";

    /**
     * Audit event bad request response json
     */
    public static final String AUDIT_EVENT_BAD_REQUEST_RESPONSE_JSON = "[{\"fieldName\": \"groupCode\", \"code\": " +
            "\"NotEmpty\", \"errorMessage\": \"must not be empty\"}, {\"fieldName\": \"initiator\", " +
            "\"code\": \"NotEmpty\", \"errorMessage\": \"must not be empty\"}, {\"fieldName\": \"message\", " +
            "\"code\": \"NotEmpty\", \"errorMessage\": \"must not be empty\"}, {\"fieldName\": \"eventId\", " +
            "\"code\": \"NotEmpty\", \"errorMessage\": \"must not be empty\"}, {\"fieldName\": \"code\", " +
            "\"code\": \"NotEmpty\", \"errorMessage\": \"must not be empty\"}]";
}
