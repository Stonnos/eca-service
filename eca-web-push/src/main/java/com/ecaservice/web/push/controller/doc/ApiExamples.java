package com.ecaservice.web.push.controller.doc;

import lombok.experimental.UtilityClass;

/**
 * Api examples utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ApiExamples {

    /**
     * Experiment request json
     */
    public static final String EXPERIMENT_REQUEST_JSON =
            "{\"id\": 1, \"requestId\": \"1d2de514-3a87-4620-9b97-c260e24340de\", \"creationDate\": " +
                    "\"2021-07-01 14:00:00\", \"startDate\": \"2021-07-01 14:00:00\", \"endDate\": " +
                    "\"2021-07-01 14:00:00\", \"requestStatus\": {\"value\": \"FINISHED\", \"description\": " +
                    "\"Experiment finished\"}, \"evaluationMethod\": {\"value\": \"CROSS_VALIDATION\", " +
                    "\"description\": \"k*V folds validation\"}, \"numFolds\": 10, \"numTests\": 1, \"seed\": 1, " +
                    "\"evaluationTotalTime\": \"00:00:1:43\", \"firstName\": \"Roman\", \"email\": \"test@mail.ru\", " +
                    "\"experimentAbsolutePath\": \"experiment_1d2de514-3a87-4620-9b97-c260e24340de.model\", " +
                    "\"trainingDataAbsolutePath\": \"data_1d2de514-3a87-4620-9b97-c260e24340de.xls\", " +
                    "\"sentDate\": \"2021-07-01 14:00:00\", \"deletedDate\": \"2021-07-01 14:00:00\", " +
                    "\"experimentType\": {\"value\": \"RANDOM_FORESTS\", \"description\": \"Random forests\"}}";
}