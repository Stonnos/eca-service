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
     * Classifiers configuration id property
     */
    public static final String CLASSIFIERS_CONFIGURATION_ID_PROPERTY = "classifiersConfigurationId";
}
