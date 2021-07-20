package com.ecaservice.external.api.controller.docs;

import lombok.experimental.UtilityClass;

/**
 * Api examples utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ApiExamples {

    public static final String EVALUATION_REQUEST_JSON =
            "{\"trainDataUrl\": \"http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls\", \"classifierOptions\": " +
                    "{\"type\": \"logistic\", \"maxIts\": 200, \"useConjugateGradientDescent\": false}, " +
                    "\"evaluationMethod\": \"CROSS_VALIDATION\", \"numFolds\": 10, \"numTests\": 1, \"seed\": 1}";
}
