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
     * Filter dictionaries cache name
     */
    public static final String FILTER_DICTIONARIES_CACHE_NAME = "filter-dictionaries";
}
