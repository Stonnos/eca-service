package com.ecaservice.server.service.push.dictionary;

import lombok.experimental.UtilityClass;

/**
 * Push properties utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class PushProperties {

    /**
     * Experiment status message type
     */
    public static final String EXPERIMENT_STATUS_MESSAGE_TYPE = "EXPERIMENT_STATUS";

    /**
     * Classifiers configuration change message type
     */
    public static final String CLASSIFIER_CONFIGURATION_CHANGE_MESSAGE_TYPE = "CLASSIFIER_CONFIGURATION_CHANGE";

    /**
     * Experiment id property
     */
    public static final String EXPERIMENT_ID_PROPERTY = "experimentId";

    /**
     * Experiment request id property
     */
    public static final String EXPERIMENT_REQUEST_ID_PROPERTY = "experimentRequestId";

    /**
     * Experiment request status property
     */
    public static final String EXPERIMENT_REQUEST_STATUS_PROPERTY = "experimentRequestStatus";

    /**
     * Classifiers configuration id property
     */
    public static final String CLASSIFIERS_CONFIGURATION_ID_PROPERTY = "classifiersConfigurationId";
}
