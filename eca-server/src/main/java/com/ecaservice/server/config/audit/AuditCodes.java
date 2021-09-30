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
}
