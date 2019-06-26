package com.ecaservice.config;

/**
 * Eca - service parameters class.
 *
 * @author Roman Batygin
 */
public class EcaServiceParam {

    /**
     * Classifiers cache name
     */
    public static final String CLASSIFIERS_CACHE_NAME = "classifiers";

    /**
     * Evaluation results cache name
     */
    public static final String EVALUATION_RESULTS_CACHE_NAME = "evaluation-results";

    /**
     * Global filter cache name
     */
    public static final String GLOBAL_FILTERS_CACHE_NAME = "global-filters";

    /**
     * Filter templates cache name
     */
    public static final String FILTER_TEMPLATES_CACHE_NAME = "filter-templates";

    /**
     * Thread pool task executor bean name for async tasks
     */
    public static final String ECA_POOL_EXECUTOR = "ecaThreadPoolTaskExecutor";
}
