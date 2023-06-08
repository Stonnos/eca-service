package com.ecaservice.server.report.model;

import lombok.Data;

/**
 * Classifier options bean.
 *
 * @author Roman Batygin
 */
@Data
public class ClassifierOptionsBean {

    /**
     * Options name
     */
    private String optionsName;

    /**
     * Creation date
     */
    private String creationDate;

    /**
     * User name
     */
    private String createdBy;

    /**
     * Json config
     */
    private String config;
}
