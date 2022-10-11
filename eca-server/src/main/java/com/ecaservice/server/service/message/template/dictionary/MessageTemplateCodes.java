package com.ecaservice.server.service.message.template.dictionary;

import lombok.experimental.UtilityClass;

/**
 * Message templates codes.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class MessageTemplateCodes {

    /**
     * New experiment template code
     */
    public static final String NEW_EXPERIMENT = "NEW_EXPERIMENT";

    /**
     * In progress experiment template code
     */
    public static final String IN_PROGRESS_EXPERIMENT = "IN_PROGRESS_EXPERIMENT";

    /**
     * Finished experiment template code
     */
    public static final String FINISHED_EXPERIMENT = "FINISHED_EXPERIMENT";

    /**
     * Error experiment template code
     */
    public static final String ERROR_EXPERIMENT = "ERROR_EXPERIMENT";

    /**
     * Timeout experiment template code
     */
    public static final String TIMEOUT_EXPERIMENT = "TIMEOUT_EXPERIMENT";

    /**
     * Set active classifiers configuration push message template code
     */
    public static final String SET_ACTIVE_CLASSIFIERS_CONFIGURATION_PUSH_MESSAGE =
            "SET_ACTIVE_CLASSIFIERS_CONFIGURATION_PUSH_MESSAGE";

    /**
     * Rename classifiers configuration push message template code
     */
    public static final String RENAME_CLASSIFIERS_CONFIGURATION_PUSH_MESSAGE =
            "RENAME_CLASSIFIERS_CONFIGURATION_PUSH_MESSAGE";

    /**
     * Add classifiers configuration options push message template code
     */
    public static final String ADD_CLASSIFIERS_CONFIGURATION_OPTIONS_PUSH_MESSAGE =
            "ADD_CLASSIFIERS_CONFIGURATION_OPTIONS_PUSH_MESSAGE";

    /**
     * Delete classifiers configuration options push message template code
     */
    public static final String DELETE_CLASSIFIERS_CONFIGURATION_OPTIONS_PUSH_MESSAGE =
            "DELETE_CLASSIFIERS_CONFIGURATION_OPTIONS_PUSH_MESSAGE";
}
