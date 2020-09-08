package com.ecaservice.report.model;

import lombok.Data;

import java.util.List;

/**
 * Classifiers configuration bean.
 *
 * @author Roman Batygin
 */
@Data
public class ClassifiersConfigurationBean {

    /**
     * Configuration name
     */
    private String configurationName;

    /**
     * Configuration created date
     */
    private String creationDate;

    /**
     * User name
     */
    private String createdBy;

    /**
     * Configuration updated date
     */
    private String updated;

    /**
     * Is active?
     */
    private boolean active;

    /**
     * Is build in?
     */
    private boolean buildIn;

    /**
     * Classifiers options count associated with configuration
     */
    private long classifiersOptionsCount;

    /**
     * Classifier options list associated with configuration
     */
    private List<ClassifierOptionsBean> classifierOptions;
}
