package com.ecaservice.server.config.audit;

import lombok.experimental.UtilityClass;

/**
 * Audit codes.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class AuditCodes {

    /**
     * Adds new classifier configuration
     */
    public static final String ADD_CONFIGURATION = "ADD_CONFIGURATION";

    /**
     * Renames classifier configuration
     */
    public static final String RENAME_CONFIGURATION = "RENAME_CONFIGURATION";

    /**
     * Deletes classifier configuration
     */
    public static final String DELETE_CONFIGURATION = "DELETE_CONFIGURATION";

    /**
     * Sets active classifier configuration
     */
    public static final String SET_ACTIVE_CONFIGURATION = "SET_ACTIVE_CONFIGURATION";

    /**
     * Copies classifier configuration
     */
    public static final String COPY_CONFIGURATION = "COPY_CONFIGURATION";

    /**
     * Adds classifier options in configuration
     */
    public static final String ADD_CLASSIFIER_OPTIONS = "ADD_CLASSIFIER_OPTIONS";

    /**
     * Deletes classifier options
     */
    public static final String DELETE_CLASSIFIER_OPTIONS = "DELETE_CLASSIFIER_OPTIONS";

    /**
     * Creates experiment request
     */
    public static final String CREATE_EXPERIMENT_REQUEST = "CREATE_EXPERIMENT_REQUEST";

    /**
     * Cancel experiment request
     */
    public static final String CANCEL_EXPERIMENT_REQUEST = "CANCEL_EXPERIMENT_REQUEST";

    /**
     * Creates evaluation request
     */
    public static final String CREATE_EVALUATION_REQUEST = "CREATE_EVALUATION_REQUEST";

    /**
     * Creates optimal evaluation request
     */
    public static final String CREATE_OPTIMAL_EVALUATION_REQUEST = "CREATE_OPTIMAL_EVALUATION_REQUEST";

    /**
     * Generates evaluation requests report
     */
    public static final String GENERATE_EVALUATION_REQUESTS_REPORT = "GENERATE_EVALUATION_REQUESTS_REPORT";

    /**
     * Generates classifiers configuration report.
     */
    public static final String GENERATE_CONFIGURATION_REPORT = "GENERATE_CONFIGURATION_REPORT";
}
