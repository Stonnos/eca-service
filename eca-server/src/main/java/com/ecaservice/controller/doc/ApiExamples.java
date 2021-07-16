package com.ecaservice.controller.doc;

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

    /**
     * Simple page request json
     */
    public static final String SIMPLE_PAGE_REQUEST_JSON = "{\"page\":0,\"size\":25}";
}
