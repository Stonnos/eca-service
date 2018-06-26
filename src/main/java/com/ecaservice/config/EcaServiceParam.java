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
     * Thread pool task executor bean name for evaluation results sending to ERS service
     */
    public static final String ERS_POOL_EXECUTOR = "ersThreadPoolTaskExecutor";

    /**
     * Simple thread pool task executor bean name
     */
    public static final String SIMPLE_POOL_EXECUTOR = "simpleAsyncTaskExecutor";
}
