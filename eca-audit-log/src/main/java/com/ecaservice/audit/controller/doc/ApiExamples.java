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
}
