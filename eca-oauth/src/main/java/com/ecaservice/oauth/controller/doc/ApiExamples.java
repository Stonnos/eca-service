package com.ecaservice.oauth.controller.doc;

import lombok.experimental.UtilityClass;

/**
 * Api examples utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ApiExamples {

    /**
     * Simple page request json
     */
    public static final String SIMPLE_PAGE_REQUEST_JSON = "{\"page\":0,\"size\":25}";

    /**
     * Unique login error response json
     */
    public static final String UNIQUE_LOGIN_RESPONSE_JSON = "[{\"fieldName\": \"login\", \"code\": \"UniqueLogin\", " +
            "\"errorMessage\": null}]";

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

    /**
     * User info response json
     */
    public static final String USER_INFO_RESPONSE_JSON = "{\"id\": 1, \"login\": \"admin\", \"email\": \"test@mail" +
            ".ru\", \"firstName\": \"Ivan\", \"lastName\": \"Ivanov\", \"middleName\": \"Ivanovich\", \"fullName\": " +
            "\"Ivanov Ivan Ivanovich\", \"creationDate\": \"2021-07-01 14:00:00\", \"tfaEnabled\": true, " +
            "\"locked\": true, \"photoId\": 1, \"passwordDate\": \"2021-07-01 14:00:00\", \"roles\": " +
            "[{\"roleName\": \"ROLE_SUPER_ADMIN\", \"description\": \"Administrator\"}]}";

    /**
     * Users page response json
     */
    public static final String USERS_PAGE_RESPONSE_JSON = "{\"content\": [{\"id\": 1, \"login\": \"admin\", " +
            "\"email\": \"test@mail.ru\", \"firstName\": \"Ivan\", \"lastName\": \"Ivanov\", \"middleName\": " +
            "\"Ivanovich\", \"fullName\": \"Ivanov Ivan Ivanovich\", \"creationDate\": \"2021-07-01 14:00:00\", " +
            "\"tfaEnabled\": true, \"locked\": true, \"photoId\": 1, \"passwordDate\": \"2021-07-01 14:00:00\", " +
            "\"roles\": [{\"roleName\": \"ROLE_SUPER_ADMIN\", \"description\": \"Administrator\"}]}], " +
            "\"page\": 0, \"totalCount\": 1}";

    /**
     * Creates user request json
     */
    public static final String CREATE_USER_REQUEST_JSON = "{\"login\": \"user\", \"email\": \"bat1238@yandex.ru\", " +
            "\"firstName\": \"Roman\", \"lastName\": \"Batygin\", \"middleName\": \"Igorevich\"}";
}
