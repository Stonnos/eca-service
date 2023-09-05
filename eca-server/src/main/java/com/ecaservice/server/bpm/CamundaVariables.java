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
     * Experiment model variable
     */
    public static final String EXPERIMENT = "experiment";

    /**
     * Error code
     */
    public static final String ERROR_CODE = "errorCode";

    /**
     * Error message
     */
    public static final String ERROR_MESSAGE = "errorMessage";

    /**
     * Experiment request data
     */
    public static final String EXPERIMENT_REQUEST_DATA = "experimentRequestData";

    /**
     * User info
     */
    public static final String USER_INFO = "userInfo";

    /**
     * Train data uuid
     */
    public static final String TRAIN_DATA_UUID = "trainDataUuid";

    /**
     * Application instance uuid
     */
    public static final String APP_INSTANCES_UUID = "appInstanceUuid";
}
