package com.ecaservice.server.model.entity;

import lombok.experimental.UtilityClass;

/**
 * Filter template type enum.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FilterTemplateType {

    /**
     * Experiment filter template type
     */
    public static final String EXPERIMENT = "EXPERIMENT";

    /**
     * Evaluation log filter template type
     */
    public static final String EVALUATION_LOG = "EVALUATION_LOG";

    /**
     * Classifier options request filter template type
     */
    public static final String CLASSIFIER_OPTIONS_REQUEST = "CLASSIFIER_OPTIONS_REQUEST";

    /**
     * Classifiers configuration filter template type
     */
    public static final String CLASSIFIERS_CONFIGURATION = "CLASSIFIERS_CONFIGURATION";

    /**
     * Classifiers configuration history filter template type
     */
    public static final String CLASSIFIERS_CONFIGURATION_HISTORY = "CLASSIFIERS_CONFIGURATION_HISTORY";

    /**
     * Instances info filter template type
     */
    public static final String INSTANCES_INFO = "INSTANCES_INFO";

    /**
     * Classifier options filter template type
     */
    public static final String CLASSIFIER_OPTIONS = "CLASSIFIER_OPTIONS";
}
