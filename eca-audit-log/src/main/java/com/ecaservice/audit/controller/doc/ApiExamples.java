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
            "{\"page\":0,\"size\":25,\"sortField\":\"eventDate\",\"ascending\":false," +
                    "\"searchQuery\":\"\",\"filters\":[{\"name\":\"groupCode\"," +
                    "\"values\":[\"USER_PROFILE_ACTIONS\"],\"matchMode\":\"EQUALS\"}," +
                    "{\"name\":\"initiator\",\"values\":[\"admin\"],\"matchMode\":\"LIKE\"}," +
                    "{\"name\":\"eventDate\",\"values\":[\"2021-07-16\"],\"matchMode\":\"RANGE\"}]}";

    /**
     * Audit event request json
     */
    public static final String AUDIT_EVENT_REQUEST_JSON = "{\"eventId\": \"a01ebc99-9c0b-4ef8-bb6d-6bb9bd380a11\", " +
            "\"message\": \"Audit message\", \"initiator\": \"user\", \"eventType\": \"START\"," +
            " \"groupCode\": \"GROUP_CODE\", \"groupTitle\": \"\", \"code\": \"AUDIT_CODE\"," +
            " \"codeTitle\": \"\", \"eventDate\": \"2021-07-16 07:57:11\"}";
}
