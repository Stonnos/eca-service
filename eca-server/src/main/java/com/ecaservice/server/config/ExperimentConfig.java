package com.ecaservice.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Experiment configuration class.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("experiment")
public class ExperimentConfig {

    /**
     * Experiment iterations number
     */
    private Integer numIterations;

    /**
     * Experiment result models size
     */
    private Integer resultSize;

    /**
     * Individual classifiers options resource path
     */
    private String individualClassifiersStoragePath;

    /**
     * Ensemble config
     */
    private EnsembleConfig ensemble;

    /**
     * Maximum fraction digits
     */
    private Integer maximumFractionDigits;

    /**
     * Experiment evaluation timeout value in minutes
     */
    private Integer evaluationTimeoutMinutes;

    /**
     * Delay value for scheduler in seconds
     */
    private Integer delaySeconds;

    /**
     * Short life url expiration in minutes
     */
    private Integer shortLifeUrlExpirationMinutes;

    /**
     * Experiment local storage path on file system
     */
    private String experimentLocalStoragePath;

    /**
     * Experiment lock ttl in seconds
     */
    private Integer lockTtlSeconds;

    /**
     * Max. requests per job
     */
    private Integer maxRequestsPerJob;

    /**
     * Ensemble configuration properties.
     */
    @Data
    public static class EnsembleConfig {

        /**
         * Number of iterations for iterative ensemble algorithms
         */
        private Integer numIterations;

        /**
         * Number of the best individual classifiers
         */
        private Integer numBestClassifiers;

        /**
         * Is concurrent classification algorithms enabled?
         */
        private Boolean multiThreadModeEnabled;

        /**
         * Specified threads number
         */
        private Integer numThreads;

        /**
         * Number of folds for Stacking algorithm using cross - validation method for
         * meta data set creation
         */
        private Integer numFoldsForStacking;
    }
}
