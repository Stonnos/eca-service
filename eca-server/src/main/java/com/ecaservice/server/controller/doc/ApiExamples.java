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

    public static final String EXPERIMENTS_PAGE_RESPONSE_JSON =
            "{\"content\": [{\"id\": 1, \"requestId\": \"1d2de514-3a87-4620-9b97-c260e24340de\", " +
                    "\"creationDate\": \"2021-07-01 14:00:00\", \"startDate\": \"2021-07-01 14:00:30\", " +
                    "\"endDate\": \"2021-07-01 14:02:13\", \"requestStatus\": {\"value\": \"FINISHED\", " +
                    "\"description\": \"Завершена\"}, \"evaluationMethod\": {\"value\": \"CROSS_VALIDATION\", " +
                    "\"description\": \"V-блочная кросс-проверка\"}, \"numFolds\": 10, \"numTests\": 1, " +
                    "\"seed\": 1, \"evaluationTotalTime\": \"00:00:1:43\", \"firstName\": \"Roman\", " +
                    "\"email\": \"test@mail.ru\", \"experimentAbsolutePath\": " +
                    "\"experiment_1d2de514-3a87-4620-9b97-c260e24340de.model\", \"trainingDataAbsolutePath\": " +
                    "\"data_1d2de514-3a87-4620-9b97-c260e24340de.xls\", \"sentDate\": \"2021-07-01 14:02:15\", " +
                    "\"deletedDate\": \"2021-07-14 14:00:00\", \"experimentType\": {\"value\": \"RANDOM_FORESTS\", " +
                    "\"description\": \"Случайные леса\"}}], \"page\": 0, \"totalCount\": 1}";

    public static final String EXPERIMENT_DETAILS_RESPONSE_JSON =
            "{\"id\": 1, \"requestId\": \"1d2de514-3a87-4620-9b97-c260e24340de\", \"creationDate\": " +
                    "\"2021-07-01 14:00:00\", \"startDate\": \"2021-07-01 14:00:30\", \"endDate\": " +
                    "\"2021-07-01 14:02:13\", \"requestStatus\": {\"value\": \"FINISHED\", " +
                    "\"description\": \"Завершена\"}, \"evaluationMethod\": {\"value\": \"CROSS_VALIDATION\", " +
                    "\"description\": \"V-блочная кросс-проверка\"}, \"numFolds\": 10, \"numTests\": 1, " +
                    "\"seed\": 1, \"evaluationTotalTime\": \"00:00:1:43\", \"firstName\": \"Roman\", " +
                    "\"email\": \"test@mail.ru\", \"experimentAbsolutePath\": " +
                    "\"experiment_1d2de514-3a87-4620-9b97-c260e24340de.model\", \"trainingDataAbsolutePath\": " +
                    "\"data_1d2de514-3a87-4620-9b97-c260e24340de.xls\", \"sentDate\": \"2021-07-01 14:02:15\", " +
                    "\"deletedDate\": \"2021-07-14 14:00:00\", \"experimentType\": {\"value\": \"RANDOM_FORESTS\", " +
                    "\"description\": \"Случайные леса\"}}";

    public static final String EXPERIMENT_TYPES_STATISTICS_RESPONSE_JSON =
            "[{\"name\": \"NEURAL_NETWORKS\", \"label\": \"Нейронные сети\", \"count\": 0}, " +
                    "{\"name\": \"HETEROGENEOUS_ENSEMBLE\", \"label\": \"Неоднородный ансамбль\", " +
                    "\"count\": 0}, {\"name\": \"MODIFIED_HETEROGENEOUS_ENSEMBLE\", \"label\": " +
                    "\"Мод. неоднородный ансамбль\", \"count\": 0}, {\"name\": \"ADA_BOOST\", " +
                    "\"label\": \"Алгоритм AdaBoost\", \"count\": 0}, {\"name\": \"STACKING\", " +
                    "\"label\": \"Алгоритм Stacking\", \"count\": 0}, {\"name\": \"KNN\", \"label\": " +
                    "\"Алгоритм KNN\", \"count\": 0}, {\"name\": \"RANDOM_FORESTS\", \"label\": " +
                    "\"Случайные леса\", \"count\": 0}, {\"name\": \"STACKING_CV\", \"label\": " +
                    "\"Алгоритм Stacking CV\", \"count\": 0}, {\"name\": \"DECISION_TREE\", " +
                    "\"label\": \"Деревья решений\", \"count\": 0}]";

    public static final String EXPERIMENT_RESULTS_DETAILS_RESPONSE_JSON =
            "{\"id\": 1, \"classifierInfo\": {\"classifierName\": \"CART\", \"inputOptions\": [{\"optionName\": " +
                    "\"Iterations number\", \"optionValue\": \"100\"}]}, \"resultsIndex\": 0, \"pctCorrect\": 99, " +
                    "\"sent\": true, \"experimentDto\": {\"id\": 1, \"requestId\": " +
                    "\"1d2de514-3a87-4620-9b97-c260e24340de\", \"creationDate\": \"2021-07-01 14:00:00\", " +
                    "\"startDate\": \"2021-07-01 14:00:30\", \"endDate\": \"2021-07-01 14:02:13\", " +
                    "\"requestStatus\": {\"value\": \"FINISHED\", \"description\": \"Завершена\"}, " +
                    "\"evaluationMethod\": {\"value\": \"CROSS_VALIDATION\", \"description\": " +
                    "\"V-блочная кросс-проверка\"}, \"numFolds\": 10, \"numTests\": 1, \"seed\": 1, " +
                    "\"evaluationTotalTime\": \"00:00:1:43\", \"firstName\": \"Roman\", \"email\": " +
                    "\"test@mail.ru\", \"experimentAbsolutePath\": " +
                    "\"experiment_1d2de514-3a87-4620-9b97-c260e24340de.model\", " +
                    "\"trainingDataAbsolutePath\": \"data_1d2de514-3a87-4620-9b97-c260e24340de.xls\", " +
                    "\"sentDate\": \"2021-07-01 14:02:15\", \"deletedDate\": \"2021-07-14 14:00:00\", " +
                    "\"experimentType\": {\"value\": \"RANDOM_FORESTS\", \"description\": " +
                    "\"Случайные леса\"}}, \"evaluationResultsDto\": {\"evaluationResultsStatus\": " +
                    "{\"value\": \"SUCCESS\", \"description\": \"Успешно\"}, \"evaluationStatisticsDto\": " +
                    "{\"numTestInstances\": 150, \"numCorrect\": 146, \"numIncorrect\": 4, \"pctCorrect\": 96, " +
                    "\"pctIncorrect\": 4, \"meanAbsoluteError\": 0.29, \"rootMeanSquaredError\": 0.01, " +
                    "\"maxAucValue\": 0.89, \"varianceError\": 0.0012, \"confidenceIntervalLowerBound\": 0.01, " +
                    "\"confidenceIntervalUpperBound\": 0.035}, \"classificationCosts\": [{\"classValue\": " +
                    "\"Iris-setosa\", \"truePositiveRate\": 0.75, \"falsePositiveRate\": 0.25, " +
                    "\"trueNegativeRate\": 0.5, \"falseNegativeRate\": 0.5, \"aucValue\": 0.9}]}}";

    public static final String EXPERIMENT_ERS_REPORT_RESPONSE_JSON =
            "{\"experimentRequestId\": \"1d2de514-3a87-4620-9b97-c260e24340de\", \"classifiersCount\": 1, " +
                    "\"sentClassifiersCount\": 1, \"experimentResults\": [{\"id\": 1, " +
                    "\"classifierInfo\": {\"classifierName\": \"CART\", \"inputOptions\": " +
                    "[{\"optionName\": \"Iterations number\", \"optionValue\": \"100\"}]}, " +
                    "\"resultsIndex\": 0, \"pctCorrect\": 99, \"sent\": true}], \"ersReportStatus\": " +
                    "{\"value\": \"SUCCESS_SENT\", \"description\": " +
                    "\"Результаты эксперимента были успешно отправлены в ERS сервис\"}}";

    public static final String EXPERIMENT_PROGRESS_RESPONSE_JSON = "{\"finished\": false, \"progress\": 85, " +
            "\"estimatedTimeLeft\": \"00:01:24\"}";

    public static final String CREATE_EXPERIMENT_RESULT_RESPONSE_JSON = "{\"id\": 1, \"requestId\": " +
            "\"1d2de514-3a87-4620-9b97-c260e24340de\"}";

    public static final String EXPERIMENT_FILTER_FIELDS_TEMPLATE_JSON = "[{\"fieldName\": \"requestId\", " +
            "\"description\": \"UUID заявки\", \"fieldOrder\": 0, \"filterFieldType\": \"TEXT\", \"matchMode\": " +
            "\"LIKE\", \"multiple\": false, \"dictionary\": null}, {\"fieldName\": \"email\", \"description\": " +
            "\"Email заявки\", \"fieldOrder\": 1, \"filterFieldType\": \"TEXT\", \"matchMode\": \"LIKE\", " +
            "\"multiple\": false, \"dictionary\": null}, {\"fieldName\": \"evaluationMethod\", " +
            "\"description\": \"Метод оценки точности\", \"fieldOrder\": 2, \"filterFieldType\": " +
            "\"REFERENCE\", \"matchMode\": \"EQUALS\", \"multiple\": false, \"dictionary\": " +
            "{\"name\": \"evaluationMethod\", \"values\": [{\"label\": \"Использование обучающего множества\", " +
            "\"value\": \"TRAINING_DATA\"}, {\"label\": \"V-блочная кросс-проверка\", \"value\": " +
            "\"CROSS_VALIDATION\"}]}}, {\"fieldName\": \"requestStatus\", \"description\": \"Статус заявки\", " +
            "\"fieldOrder\": 3, \"filterFieldType\": \"REFERENCE\", \"matchMode\": \"EQUALS\", \"multiple\": false, " +
            "\"dictionary\": {\"name\": \"requestStatus\", \"values\": [{\"label\": \"Новая\", \"value\": " +
            "\"NEW\"}, {\"label\": \"Завершена\", \"value\": \"FINISHED\"}, {\"label\": \"Ошибка\", " +
            "\"value\": \"ERROR\"}, {\"label\": \"Таймаут\", \"value\": \"TIMEOUT\"}, {\"label\": " +
            "\"В работе\", \"value\": \"IN_PROGRESS\"}]}}, {\"fieldName\": \"experimentType\", " +
            "\"description\": \"Тип эксперимента\", \"fieldOrder\": 4, \"filterFieldType\": " +
            "\"REFERENCE\", \"matchMode\": \"EQUALS\", \"multiple\": true, \"dictionary\": " +
            "{\"name\": \"experimentType\", \"values\": [{\"label\": \"Нейронные сети\", \"value\": " +
            "\"NEURAL_NETWORKS\"}, {\"label\": \"Алгоритм AdaBoost\", \"value\": \"ADA_BOOST\"}, " +
            "{\"label\": \"Алгоритм Stacking\", \"value\": \"STACKING\"}, {\"label\": \"Случайные леса\", " +
            "\"value\": \"RANDOM_FORESTS\"}, {\"label\": \"Алгоритм Stacking CV\", \"value\": " +
            "\"STACKING_CV\"}, {\"label\": \"Деревья решений\", \"value\": \"DECISION_TREE\"}, {\"label\": " +
            "\"Неоднородный ансамбль\", \"value\": \"HETEROGENEOUS_ENSEMBLE\"}, {\"label\": " +
            "\"Мод. неоднородный ансамбль\", \"value\": \"MODIFIED_HETEROGENEOUS_ENSEMBLE\"}, {\"label\": " +
            "\"Алгоритм KNN\", \"value\": \"KNN\"}]}}, {\"fieldName\": \"creationDate\", \"description\": " +
            "\"Дата создания заявки\", \"fieldOrder\": 5, \"filterFieldType\": \"DATE\", \"matchMode\": " +
            "\"RANGE\", \"multiple\": true, \"dictionary\": null}, {\"fieldName\": \"sentDate\", \"description\": " +
            "\"Дата отправки результатов\", \"fieldOrder\": 6, \"filterFieldType\": \"DATE\", \"matchMode\": " +
            "\"RANGE\", \"multiple\": true, \"dictionary\": null}]";

    public static final String EXPERIMENT_TYPES_RESPONSE_JSON =
            "{\"name\": \"experimentType\", \"values\": [{\"label\": \"Нейронные сети\", \"value\": " +
                    "\"NEURAL_NETWORKS\"}, {\"label\": \"Алгоритм AdaBoost\", \"value\": \"ADA_BOOST\"}, " +
                    "{\"label\": \"Алгоритм Stacking\", \"value\": \"STACKING\"}, {\"label\": \"Случайные леса\", " +
                    "\"value\": \"RANDOM_FORESTS\"}, {\"label\": \"Алгоритм Stacking CV\", \"value\": " +
                    "\"STACKING_CV\"}, {\"label\": \"Деревья решений\", \"value\": \"DECISION_TREE\"}, " +
                    "{\"label\": \"Неоднородный ансамбль\", \"value\": \"HETEROGENEOUS_ENSEMBLE\"}, " +
                    "{\"label\": \"Мод. неоднородный ансамбль\", \"value\": " +
                    "\"MODIFIED_HETEROGENEOUS_ENSEMBLE\"}, {\"label\": \"Алгоритм KNN\", \"value\": \"KNN\"}]}";

    public static final String EVALUATION_LOGS_FILTER_TEMPLATE_JSON = "[{\"fieldName\": \"requestId\", " +
            "\"description\": \"UUID заявки\", \"fieldOrder\": 0, \"filterFieldType\": \"TEXT\", " +
            "\"matchMode\": \"LIKE\", \"multiple\": false, \"dictionary\": null}, {\"fieldName\": " +
            "\"classifierInfo.classifierName\", \"description\": \"Классификатор\", \"fieldOrder\": 1, " +
            "\"filterFieldType\": \"TEXT\", \"matchMode\": \"LIKE\", \"multiple\": false, " +
            "\"dictionary\": null}, {\"fieldName\": \"instancesInfo.relationName\", \"description\": " +
            "\"Обучающая выборка\", \"fieldOrder\": 2, \"filterFieldType\": \"TEXT\", \"matchMode\": " +
            "\"LIKE\", \"multiple\": false, \"dictionary\": null}, {\"fieldName\": \"evaluationMethod\", " +
            "\"description\": \"Метод оценки точности\", \"fieldOrder\": 3, \"filterFieldType\": " +
            "\"REFERENCE\", \"matchMode\": \"EQUALS\", \"multiple\": false, \"dictionary\": {\"name\": " +
            "\"evaluationMethod\", \"values\": [{\"label\": \"Использование обучающего множества\", " +
            "\"value\": \"TRAINING_DATA\"}, {\"label\": \"V-блочная кросс-проверка\", \"value\": " +
            "\"CROSS_VALIDATION\"}]}}, {\"fieldName\": \"requestStatus\", \"description\": " +
            "\"Статус заявки\", \"fieldOrder\": 4, \"filterFieldType\": \"REFERENCE\", \"matchMode\": " +
            "\"EQUALS\", \"multiple\": false, \"dictionary\": {\"name\": \"requestStatus\", " +
            "\"values\": [{\"label\": \"Новая\", \"value\": \"NEW\"}, {\"label\": \"Завершена\", " +
            "\"value\": \"FINISHED\"}, {\"label\": \"Ошибка\", \"value\": \"ERROR\"}, {\"label\": " +
            "\"Таймаут\", \"value\": \"TIMEOUT\"}, {\"label\": \"В работе\", \"value\": " +
            "\"IN_PROGRESS\"}]}}, {\"fieldName\": \"creationDate\", \"description\": \"Дата создания заявки\", " +
            "\"fieldOrder\": 5, \"filterFieldType\": \"DATE\", \"matchMode\": \"RANGE\", \"multiple\": true, " +
            "\"dictionary\": null}]";

    public static final String EVALUATION_METHODS_RESPONSE_JSON = "{\"name\": \"evaluationMethod\", \"values\": " +
            "[{\"label\": \"Использование обучающего множества\", \"value\": \"TRAINING_DATA\"}, " +
            "{\"label\": \"V-блочная кросс-проверка\", \"value\": \"CROSS_VALIDATION\"}]}";

    public static final String CLASSIFIER_OPTIONS_REQUESTS_FILTER_TEMPLATE_JSON = "[{\"fieldName\": \"requestId\", " +
            "\"description\": \"UUID заявки\", \"fieldOrder\": 0, \"filterFieldType\": \"TEXT\", " +
            "\"matchMode\": \"LIKE\", \"multiple\": false, \"dictionary\": null}, " +
            "{\"fieldName\": \"relationName\", \"description\": " +
            "\"Обучающая выборка\", \"fieldOrder\": 1, \"filterFieldType\": \"TEXT\", \"matchMode\": \"LIKE\", " +
            "\"multiple\": false, \"dictionary\": null}, {\"fieldName\": \"evaluationMethod\", \"description\": " +
            "\"Метод оценки точности\", \"fieldOrder\": 2, \"filterFieldType\": \"REFERENCE\", \"matchMode\": " +
            "\"EQUALS\", \"multiple\": false, \"dictionary\": {\"name\": \"evaluationMethod\", \"values\": " +
            "[{\"label\": \"Использование обучающего множества\", \"value\": \"TRAINING_DATA\"}, {\"label\": " +
            "\"V-блочная кросс-проверка\", \"value\": \"CROSS_VALIDATION\"}]}}, {\"fieldName\": " +
            "\"responseStatus\", \"description\": \"Статус ответа от ERS\", \"fieldOrder\": 3, " +
            "\"filterFieldType\": \"REFERENCE\", \"matchMode\": \"EQUALS\", \"multiple\": false, " +
            "\"dictionary\": {\"name\": \"ersResponseStatus\", \"values\": [{\"label\": \"Успешно\", " +
            "\"value\": \"SUCCESS\"}, {\"label\": \"Заявка с таким UUID уже существует\", \"value\": " +
            "\"DUPLICATE_REQUEST_ID\"}, {\"label\": \"Ошибка\", \"value\": \"ERROR\"}, " +
            "{\"label\": \"Не найдена обучающая выборка\", \"value\": \"DATA_NOT_FOUND\"}, " +
            "{\"label\": \"Не найдены оптимальные конфигурации моделей\", \"value\": " +
            "\"RESULTS_NOT_FOUND\"}]}}, {\"fieldName\": \"requestDate\", \"description\": " +
            "\"Дата отправки запроса в ERS\", \"fieldOrder\": 4, \"filterFieldType\": " +
            "\"DATE\", \"matchMode\": \"RANGE\", \"multiple\": true, \"dictionary\": null}]";
}
