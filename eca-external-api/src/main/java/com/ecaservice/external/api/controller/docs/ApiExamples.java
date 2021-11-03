package com.ecaservice.external.api.controller.docs;

import lombok.experimental.UtilityClass;

/**
 * Api examples utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ApiExamples {


    /**
     * Evaluation request json
     */
    public static final String EVALUATION_REQUEST_JSON =
            "{\"trainDataUrl\": \"http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls\", \"classifierOptions\": " +
                    "{\"type\": \"logistic\", \"maxIts\": 200, \"useConjugateGradientDescent\": false}, " +
                    "\"evaluationMethod\": \"CROSS_VALIDATION\", \"numFolds\": 10, \"numTests\": 1, \"seed\": 1}";

    /**
     * Instances request json
     */
    public static final String INSTANCES_REQUEST_JSON =
            "{\"trainDataUrl\": \"http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls\"}";

    /**
     * Upload instances response json
     */
    public static final String UPLOAD_INSTANCES_RESPONSE_JSON = "{\"payload\": {\"dataId\": " +
            "\"2a35bffe-27ad-4a50-a7e0-8c871cfd7cc5\", \"dataUrl\": \"data://2a35bffe-27ad-4a50-a7e0-8c871cfd7cc5\"}, " +
            "\"responseCode\": \"SUCCESS\", \"errorDescription\": null}";

    /**
     * Evaluation request response json
     */
    public static final String EVALUATION_REQUEST_RESPONSE_JSON = "{\"payload\": {\"requestId\": " +
            "\"1cbe6c49-8432-4c81-9afa-90f04a803fed\", \"evaluationStatus\": \"IN_PROGRESS\", " +
            "\"errorCode\": null, " +
            "\"modelUrl\": null, \"numTestInstances\": null, \"numCorrect\": null, \"numIncorrect\": null, " +
            "\"pctCorrect\": null, \"pctIncorrect\": null, \"meanAbsoluteError\": null}, \"responseCode\": " +
            "\"SUCCESS\", \"errorDescription\": null}";

    /**
     * Evaluation status response json
     */
    public static final String EVALUATION_STATUS_RESPONSE_JSON = "{\"payload\": {\"requestId\": " +
            "\"1cbe6c49-8432-4c81-9afa-90f04a803fed\", \"evaluationStatus\": \"FINISHED\", " +
            "\"errorCode\": null, \"modelUrl\": " +
            "\"http://localhost:8080/external-api/download-model/1cbe6c49-8432-4c81-9afa-90f04a803fed\", " +
            "\"numTestInstances\": 150, \"numCorrect\": 144, \"numIncorrect\": 6, \"pctCorrect\": 96, " +
            "\"pctIncorrect\": 4, \"meanAbsoluteError\": 0.02869334024628254}, \"responseCode\": \"SUCCESS\", " +
            "\"errorDescription\": null}";

    /**
     * Unauthorized error response json
     */
    public static final String UNAUTHORIZED_RESPONSE_JSON = "{\"error\": \"unauthorized\", \"error_description\": " +
            "\"Full authentication is required to access this resource\"}";

    /**
     * Validation error response json
     */
    public static final String VALIDATION_ERROR_RESPONSE_JSON = "{\"payload\": [{\"fieldName\": null, \"code\": " +
            "\"DataNotFound\", \"errorMessage\": \"Entity with search key [1] not found!\"}], " +
            "\"responseCode\": \"VALIDATION_ERROR\", \"errorDescription\": \"Validation errors\"}";

    /**
     * Invalid train data response json
     */
    public static final String INVALID_TRAIN_DATA_RESPONSE_JSON = "{\"payload\": [{\"fieldName\": \"trainingData\", " +
            "\"code\": \"ValidTrainData\", \"errorMessage\": \"Invalid train data extension. " +
            "Must be one of xls, xlsx, csv, arff, json, xml, txt, data, docx\"}], \"responseCode\": " +
            "\"VALIDATION_ERROR\", \"errorDescription\": \"Validation errors\"}";

    /**
     * Invalid evaluation request response json
     */
    public static final String INVALID_EVALUATION_REQUEST_RESPONSE_JSON = "{\"payload\": [{\"fieldName\": " +
            "\"classifierOptions\", \"code\": \"NotNull\", \"errorMessage\": \"must not be null\"}, " +
            "{\"fieldName\": \"evaluationMethod\", \"code\": \"NotNull\", \"errorMessage\": \"must not be null\"}], " +
            "\"responseCode\": \"VALIDATION_ERROR\", \"errorDescription\": \"Validation errors\"}";

    /**
     * Invalid instances request response json
     */
    public static final String INVALID_INSTANCES_REQUEST_RESPONSE_JSON = "{\"payload\": [{\"fieldName\": " +
            "\"trainDataUrl\", \"code\": \"DataURL\", \"errorMessage\": \"train data url must have one of the " +
            "protocols such as http, ftp, data\"}], \"responseCode\": \"VALIDATION_ERROR\", " +
            "\"errorDescription\": \"Validation errors\"}";
}
