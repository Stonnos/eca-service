package com.ecaservice.config;

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
     * Experiment file format
     */
    private String fileFormat;

    /**
     * Experiment storage path
     */
    private String storagePath;

    /**
     * Individual classifiers options resource path
     */
    private String individualClassifiersStoragePath;

    /**
     * Data config
     */
    private DataConfig data;

    /**
     * Ensemble config
     */
    private EnsembleConfig ensemble;

    /**
     * Experiment download url
     */
    private String downloadUrl;

    /**
     * Maximum fraction digits
     */
    private Integer maximumFractionDigits;

    /**
     * Number of days for experiment storage
     */
    private Long numberOfDaysForStorage;

    /**
     * Experiment timeout value in hours
     */
    private Integer timeout;

    /**
     * Delay value for scheduler in seconds
     */
    private Integer delaySeconds;

    /**
     * Experiment result models size for sending to ERS web - service
     */
    private Integer resultSizeToSend;

    /**
     * Data configuration properties.
     */
    @Data
    public static class DataConfig {

        /**
         * Data file format
         */
        private String fileFormat;

        /**
         * Data storage path
         */
        private String storagePath;

        /**
         * Date format
         */
        private String dateFormat;
    }

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
         * Number of best individual classifiers
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
