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
     * User email not exists error response json
     */
    public static final String USER_EMAIL_RESPONSE_JSON = "[{\"fieldName\": \"email\", \"code\": \"UserEmail\", " +
            "\"errorMessage\": null}]";

    /**
     * User email must be unique response json
     */
    public static final String UNIQUE_EMAIL_RESPONSE_JSON = "[{\"fieldName\": \"email\", \"code\": \"UniqueEmail\", " +
            "\"errorMessage\": \"Can't set user email because its exists\"}]";

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

    /**
     * Update user info request json
     */
    public static final String UPDATE_USER_INFO_REQUEST_JSON = "{\"firstName\": \"Roman\", \"lastName\": \"Batygin\"," +
            " \"middleName\": \"Igorevich\"}";

    /**
     * Change password request json
     */
    public static final String CHANGE_PASSWORD_REQUEST_JSON = "{\"oldPassword\": \"oldPassw0rd!\", \"newPassword\": " +
            "\"newPassw0rd!\"}";

    /**
     * Forgot password request json
     */
    public static final String FORGOT_PASSWORD_REQUEST_JSON = "{\"email\": \"bat1238@yandex.ru\"}";

    /**
     * Reset password request json
     */
    public static final String RESET_PASSWORD_REQUEST_JSON = "{\"token\": " +
            "\"MDhmNTg4MDdiMTI0Y2Y4OWNmN2UxYmE1OTljYjUzOWU6MTYxNjE1MzM4MDMzMQ==\", \"password\": \"passw0rd!\"}";

    /**
     * Invalid token response json
     */
    public static final String INVALID_TOKEN_RESPONSE_JSON = "[{\"fieldName\": null, \"code\": \"InvalidToken\", " +
            "\"errorMessage\": \"Invalid token\"}]";

    /**
     * Invalid password response json
     */
    public static final String INVALID_PASSWORD_RESPONSE_JSON = "[{\"fieldName\": null, \"code\": " +
            "\"InvalidPassword\", \"errorMessage\": \"Invalid password\"}]";

    /**
     * Invalid update user info request response json
     */
    public static final String INVALID_UPDATE_USER_INFO_REQUEST_RESPONSE_JSON =
            "[{\"fieldName\": \"lastName\", \"code\": \"Size\", \"errorMessage\": \"size must be between 2 and 30\"}, " +
                    "{\"fieldName\": \"middleName\", \"code\": \"Size\", \"errorMessage\": " +
                    "\"size must be between 2 and 30\"}, {\"fieldName\": \"firstName\", \"code\": \"Size\", " +
                    "\"errorMessage\": \"size must be between 2 and 30\"}]";
}
