package com.ecaservice.web.dto.doc;

import lombok.experimental.UtilityClass;

/**
 * Api examples common utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class CommonApiExamples {

    /**
     * Simple page request json
     */
    public static final String SIMPLE_PAGE_REQUEST_JSON = "{\"page\":0,\"size\":25}";

    /**
     * Invalid page request response json
     */
    public static final String INVALID_PAGE_REQUEST_RESPONSE_JSON =
            "[{\"fieldName\": \"page\", \"code\": \"Min\", \"errorMessage\": " +
                    "\"must be greater than or equal to 0\"}, {\"fieldName\": \"size\", \"code\": \"Min\", " +
                    "\"errorMessage\": \"must be greater than or equal to 1\"}]";

    /**
     * Unauthorized error response json
     */
    public static final String UNAUTHORIZED_RESPONSE_JSON = "{\"error\": \"unauthorized\", \"error_description\": " +
            "\"Full authentication is required to access this resource\"}";

    /**
     * Access denied response json
     */
    public static final String ACCESS_DENIED_RESPONSE_JSON = "{\"error\": \"access_denied\", " +
            "\"error_description\": \"Access is denied\"}";

    /**
     * Data not found response json
     */
    public static final String DATA_NOT_FOUND_RESPONSE_JSON = "[{\"fieldName\": null, \"code\": \"DataNotFound\", " +
            "\"errorMessage\": \"Entity with search key [1] not found!\"}]";
}
