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
            "{\"receiver\": \"test@mail.ru\", \"templateCode\": \"NEW_EXPERIMENT\", \"variables\": " +
                    "{\"firstName\": \"Роман\", \"requestId\": \"1cec4e54-0f46-4d70-ad19-a8f9f1a0e33c\", " +
                    "\"experimentType\": \"KNN\"},\"priority\": 1}";
}
