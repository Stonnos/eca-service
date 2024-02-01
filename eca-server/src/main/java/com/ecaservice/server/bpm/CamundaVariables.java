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
     * Evaluation log id variable
     */
    public static final String EVALUATION_LOG_ID = "evaluationLogId";

    /**
     * Experiment model variable
     */
    public static final String EXPERIMENT = "experiment";

    /**
     * Evaluation log model variable
     */
    public static final String EVALUATION_LOG = "evaluationLog";

    /**
     * Error code
     */
    public static final String ERROR_CODE = "errorCode";

    /**
     * Error message
     */
    public static final String ERROR_MESSAGE = "errorMessage";

    /**
     * Evaluation request data
     */
    public static final String EVALUATION_REQUEST_DATA = "evaluationRequestData";

    /**
     * Evaluation results data
     */
    public static final String EVALUATION_RESULTS_DATA = "evaluationResultsData";

    /**
     * Classifier options result
     */
    public static final String CLASSIFIER_OPTIONS_RESULT = "classifierOptionsResult";

    /**
     * User info
     */
    public static final String USER_INFO = "userInfo";

    /**
     * Email message template code
     */
    public static final String EMAIL_TEMPLATE_CODE = "emailTemplateCode";

    /**
     * Email message template variables
     */
    public static final String EMAIL_TEMPLATE_VARIABLES = "emailTemplateVariables";

    /**
     * Push message type
     */
    public static final String PUSH_MESSAGE_TYPE = "pushMessageType";

    /**
     * Push message properties
     */
    public static final String PUSH_MESSAGE_PROPERTIES = "pushMessageProperties";

    /**
     * Push message template code
     */
    public static final String PUSH_TEMPLATE_CODE = "pushTemplateCode";

    /**
     * Push message template context variable
     */
    public static final String PUSH_TEMPLATE_CONTEXT_VARIABLE = "pushTemplateContextVariable";

    /**
     * Evaluation request status
     */
    public static final String EVALUATION_REQUEST_STATUS = "requestStatus";

    /**
     * User profile options
     */
    public static final String USER_PROFILE_OPTIONS = "userProfileOptions";

    /**
     * User login
     */
    public static final String USER_LOGIN = "userLogin";
}
