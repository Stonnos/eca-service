package com.ecaservice.mail.controller.docs;

import lombok.experimental.UtilityClass;

/**
 * Api examples utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ApiExamples {

    public static final String EMAIL_REQUEST_JSON =
            "{\"requestId\": \"1d2de514-3a87-4620-9b97-c260e24340de\", " +
            "\"receiver\": \"test@mail.ru\", \"templateCode\": \"NEW_EXPERIMENT\", \"variables\": " +
                    "{\"firstName\": \"Роман\", \"requestId\": \"1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c\", " +
                    "\"experimentType\": \"KNN\"},\"priority\": 1}";

    /**
     * Invalid template code response json
     */
    public static final String INVALID_TEMPLATE_CODE_RESPONSE_JSON = "[{\"fieldName\": \"templateCode\", \"code\": " +
            "\"ValidEmailRequest\", \"errorMessage\": \"Invalid template code!\"}]";

    /**
     * Email templates page response json
     */
    public static final String EMAIL_TEMPLATES_PAGE_RESPONSE_JSON = "{\"content\": [{\"id\": 1, \"created\": " +
            "\"2021-07-01 14:00:00\", \"code\": \"NEW_EXPERIMENT\", \"description\": \"New experiment\", " +
            "\"subject\": \"New experiment request\", \"body\": \"some body\", \"parameters\": [{\"id\": 1, " +
            "\"created\": \"2021-07-01 14:00:00\", \"parameterName\": \"requestId\", \"description\": " +
            "\"Experiment request id\"}]}], \"page\": 0, \"totalCount\": 1}";

    /**
     * Email response json
     */
    public static final String EMAIL_RESPONSE_JSON = "{\"requestId\": \"1d2de514-3a87-4620-9b97-c260e24340de\"}";
}
