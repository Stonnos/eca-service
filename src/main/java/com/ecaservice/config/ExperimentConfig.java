package com.ecaservice.config;

import com.ecaservice.model.experiment.ExperimentStatus;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

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
     * Experiment timeout value in hours
     */
    private Integer timeout;

    /**
     * Delay value for scheduler in seconds
     */
    private Integer delaySeconds;

    /**
     * Process statuses list
     */
    private List<ExperimentStatus> processStatuses;

    /**
     * Sent statuses list
     */
    private List<ExperimentStatus> sentStatuses;

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
         * Number of iterations
         */
        private Integer numIterations;
    }

}
