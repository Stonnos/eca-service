package com.ecaservice.server.bpm;

import lombok.experimental.UtilityClass;

/**
 * Camunda variables names constants.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class CamundaVariables {

    /**
     * Task type variable
     */
    public static final String TASK_TYPE = "taskType";

    /**
     * Experiment id variable
     */
    public static final String EXPERIMENT_ID = "experimentId";

    /**
     * Error code
     */
    public static final String ERROR_CODE = "errorCode";

    /**
     * Error message
     */
    public static final String ERROR_MESSAGE = "errorMessage";
}
