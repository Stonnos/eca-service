package com.ecaservice.server.controller.doc;

import lombok.experimental.UtilityClass;

/**
 * Api examples utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ApiExamples {

    /**
     * Experiments page request json example
     */
    public static final String EXPERIMENTS_PAGE_REQUEST_JSON =
            "{\"page\":0,\"size\":25,\"sortField\":\"creationDate\",\"ascending\":false," +
                    "\"searchQuery\":\"\",\"filters\":[{\"name\":\"evaluationMethod\"," +
                    "\"values\":[\"CROSS_VALIDATION\"],\"matchMode\":\"EQUALS\"},{\"name\":\"requestStatus\"," +
                    "\"values\":[\"FINISHED\"],\"matchMode\":\"EQUALS\"},{\"name\":\"experimentType\"," +
                    "\"values\":[\"ADA_BOOST\",\"NEURAL_NETWORKS\",\"HETEROGENEOUS_ENSEMBLE\"," +
                    "\"MODIFIED_HETEROGENEOUS_ENSEMBLE\"],\"matchMode\":\"EQUALS\"}," +
                    "{\"name\":\"creationDate\",\"values\":[\"2021-07-12\"],\"matchMode\":\"RANGE\"}]}";

    /**
     * Evaluation logs page request json example
     */
    public static final String EVALUATION_LOGS_PAGE_REQUEST_JSON = "{\"page\":0,\"size\":25," +
            "\"sortField\":\"creationDate\",\"ascending\":false,\"searchQuery\":\"\"," +
            "\"filters\":[{\"name\":\"evaluationMethod\",\"values\":[\"CROSS_VALIDATION\"]," +
            "\"matchMode\":\"EQUALS\"},{\"name\":\"requestStatus\",\"values\":[\"FINISHED\"]," +
            "\"matchMode\":\"EQUALS\"},{\"name\":\"creationDate\",\"values\":[\"2021-07-12\"]," +
            "\"matchMode\":\"RANGE\"}]}";

    /**
     * Classifier options requests page request json example
     */
    public static final String CLASSIFIER_OPTIONS_REQUESTS_PAGE_REQUEST_JSON =
            "{\"page\":0,\"size\":25,\"sortField\":\"requestDate\",\"ascending\":false," +
                    "\"searchQuery\":\"\",\"filters\":[{\"name\":\"evaluationMethod\"," +
                    "\"values\":[\"CROSS_VALIDATION\"],\"matchMode\":\"EQUALS\"}," +
                    "{\"name\":\"responseStatus\",\"values\":[\"SUCCESS\"]," +
                    "\"matchMode\":\"EQUALS\"},{\"name\":\"requestDate\",\"values\":[\"2021-07-16\"]," +
                    "\"matchMode\":\"RANGE\"}]}";


    public static final String UPDATE_CLASSIFIERS_CONFIGURATION_REQUEST_JSON =
            "{\"id\": 1, \"configurationName\": \"Classifiers configuration\"}";

    public static final String UPDATE_CLASSIFIERS_CONFIGURATION_BAD_REQUEST_RESPONSE_JSON =
            "[{\"fieldName\": \"configurationName\", \"code\": \"NotBlank\", \"errorMessage\": \"must not be blank\"}]";

    public static final String CREATE_CLASSIFIERS_CONFIGURATION_REQUEST_JSON =
            "{\"configurationName\": \"Classifiers configuration\"}";

    public static final String CREATE_CLASSIFIERS_CONFIGURATION_BAD_REQUEST_RESPONSE_JSON =
            "[{\"fieldName\": \"configurationName\", \"code\": \"NotBlank\", \"errorMessage\": \"must not be blank\"}]";

    public static final String GET_CLASSIFIERS_CONFIGURATIONS_PAGE_RESPONSE_JSON = "{\"content\": [{\"id\": 1, " +
            "\"configurationName\": \"Default configuration\", \"creationDate\": \"2021-07-01 14:00:00\", " +
            "\"createdBy\": \"admin\", \"updated\": \"2021-07-01 14:00:00\", \"active\": true, " +
            "\"buildIn\": true, \"classifiersOptionsCount\": 25}], \"page\": 0, \"totalCount\": 1}";

    public static final String COPY_CLASSIFIERS_CONFIGURATION_REQUEST_JSON =
            "{\"id\": 1, \"configurationName\": \"Classifiers configuration\"}";

    public static final String COPY_CLASSIFIERS_CONFIGURATION_BAD_REQUEST_RESPONSE_JSON =
            "[{\"fieldName\": \"configurationName\", \"code\": \"NotBlank\", \"errorMessage\": \"must not be blank\"}]";

    public static final String CLASSIFIERS_CONFIGURATION_DETAILS_RESPONSE_JSON =
            "{\"id\": 1, \"configurationName\": \"Default configuration\", \"creationDate\": " +
                    "\"2021-07-01 14:00:00\", \"createdBy\": \"admin\", \"updated\": \"2021-07-01 14:00:00\", " +
                    "\"active\": true, \"buildIn\": true, \"classifiersOptionsCount\": 25}";

    public static final String GET_CLASSIFIERS_OPTIONS_PAGE_RESPONSE_JSON = "{\"content\": [{\"id\": 1, " +
            "\"optionsName\": \"DecisionTreeOptions\", \"creationDate\": \"2021-07-01 14:00:00\", " +
            "\"createdBy\": \"admin\", \"config\": \"Json config\"}], \"page\": 0, \"totalCount\": 1}";

    public static final String GET_CLASSIFIERS_OPTIONS_LIST_RESPONSE_JSON =
            "[{\"id\": 1, \"optionsName\": \"DecisionTreeOptions\", \"creationDate\": \"2021-07-01 14:00:00\", " +
                    "\"createdBy\": \"admin\", \"config\": \"Json config\"}]";

    public static final String SAVE_CLASSIFIER_OPTIONS_RESPONSE_JSON =
            "{\"id\": 1, \"sourceFileName\": \"cart.json\", \"success\": true, \"errorMessage\": \"\"}";

    public static final String EVALUATION_LOGS_PAGE_RESPONSE_JSON =
            "{\"content\": [{\"id\": 1, \"requestId\": \"1d2de514-3a87-4620-9b97-c260e24340de\", " +
                    "\"creationDate\": \"2021-07-01 14:00:00\", \"startDate\": \"2021-07-01 14:00:01\", " +
                    "\"endDate\": \"2021-07-01 14:00:12\", \"requestStatus\": {\"value\": \"FINISHED\", " +
                    "\"description\": \"Завершена\"}, \"evaluationMethod\": {\"value\": \"CROSS_VALIDATION\", " +
                    "\"description\": \"V-блочная кросс-проверка\"}, \"numFolds\": 10, \"numTests\": 1, " +
                    "\"seed\": 1, \"evaluationTotalTime\": \"00:00:00:11\", \"classifierInfo\": " +
                    "{\"classifierName\": \"CART\", \"inputOptions\": [{\"optionName\": \"Iterations number\", " +
                    "\"optionValue\": \"100\"}]}, \"instancesInfo\": {\"relationName\": \"iris\", " +
                    "\"numInstances\": 150, \"numAttributes\": 5, \"numClasses\": 4, \"className\": " +
                    "\"class\"}}], \"page\": 0, \"totalCount\": 1}";

    public static final String EVALUATION_LOG_DETAILS_RESPONSE_JSON =
            "{\"id\": 1, \"requestId\": \"1d2de514-3a87-4620-9b97-c260e24340de\", \"creationDate\": " +
                    "\"2021-07-01 14:00:00\", \"startDate\": \"2021-07-01 14:00:01\", \"endDate\": " +
                    "\"2021-07-01 14:00:12\", \"requestStatus\": {\"value\": \"FINISHED\", " +
                    "\"description\": \"Завершена\"}, \"evaluationMethod\": {\"value\": \"CROSS_VALIDATION\", " +
                    "\"description\": \"V-блочная кросс-проверка\"}, \"numFolds\": 10, \"numTests\": 1, " +
                    "\"seed\": 1, \"evaluationTotalTime\": \"00:00:00:11\", \"classifierInfo\": " +
                    "{\"classifierName\": \"CART\", \"inputOptions\": [{\"optionName\": \"Iterations number\", " +
                    "\"optionValue\": \"100\"}]}, \"instancesInfo\": {\"relationName\": \"iris\", " +
                    "\"numInstances\": 150, \"numAttributes\": 5, \"numClasses\": 4, \"className\": " +
                    "\"class\"}, \"evaluationResultsDto\": {\"evaluationResultsStatus\": {\"value\": " +
                    "\"RESULTS_RECEIVED\", \"description\": \"Получены результаты классификации\"}, " +
                    "\"evaluationStatisticsDto\": {\"numTestInstances\": 150, \"numCorrect\": 146, " +
                    "\"numIncorrect\": 4, \"pctCorrect\": 96, \"pctIncorrect\": 4, \"meanAbsoluteError\": 0.29, " +
                    "\"rootMeanSquaredError\": 0.01, \"maxAucValue\": 0.89, \"varianceError\": 0.0012, " +
                    "\"confidenceIntervalLowerBound\": 0.01, \"confidenceIntervalUpperBound\": 0.035}, " +
                    "\"classificationCosts\": [{\"classValue\": \"Iris-setosa\", \"truePositiveRate\": 0.75, " +
                    "\"falsePositiveRate\": 0.25, \"trueNegativeRate\": 0.5, \"falseNegativeRate\": 0.5, " +
                    "\"aucValue\": 0.9}]}}";

    public static final String REQUESTS_STATUSES_STATISTICS_RESPONSE_JSON =
            "{\"totalCount\": 100, \"newRequestsCount\": 0, \"inProgressRequestsCount\": 1, " +
                    "\"finishedRequestsCount\": 99, \"timeoutRequestsCount\": 0, \"errorRequestsCount\": 0}";

    public static final String CLASSIFIER_OPTIONS_REQUESTS_PAGE_RESPONSE_JSON =
            "{\"content\": [{\"requestDate\": \"2021-07-01 14:00:00\", \"requestId\": " +
                    "\"1d2de514-3a87-4620-9b97-c260e24340de\", \"relationName\": \"glass\", " +
                    "\"evaluationMethod\": {\"value\": \"CROSS_VALIDATION\", \"description\": " +
                    "\"V-блочная кросс-проверка\"}, \"numFolds\": 10, \"numTests\": 1, \"seed\": 1, " +
                    "\"responseStatus\": {\"value\": \"SUCCESS\", \"description\": \"Успешно\"}, " +
                    "\"classifierOptionsResponseModels\": [{\"classifierName\": \"CART\", \"options\": " +
                    "\"json config\"}]}], \"page\": 0, \"totalCount\": 1}";
}
