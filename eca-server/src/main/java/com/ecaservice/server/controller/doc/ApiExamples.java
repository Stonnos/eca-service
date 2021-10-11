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
            "{\"id\": 1, \"sourceFileName\": \"cart.json\", \"success\": true, \"errorMessage\": \"string\"}";
}
