package com.ecaservice.server.config.cache;

import lombok.experimental.UtilityClass;

/**
 * Cache names utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class CacheNames {

    /**
     * Evaluation results cache name
     */
    public static final String EVALUATION_RESULTS_CACHE_NAME = "evaluationResults";

    /**
     * Evaluation logs total count query
     */
    public static final String EVALUATION_LOGS_TOTAL_COUNT_QUERY = "evaluationLogsTotalCountQuery";

    /**
     * Experiments total count query
     */
    public static final String EXPERIMENTS_TOTAL_COUNT_QUERY = "experimentsTotalCountQuery";
}
