package com.ecaservice.server.model;

import lombok.Data;

/**
 * Classifier options result.
 *
 * @author Roman Batygin
 */
@Data
public class ClassifierOptionsResult {

    /**
     * Classifier options json
     */
    private String optionsJson;

    /**
     * Is classifier options found?
     */
    private boolean found;

    /**
     * Error message
     */
    private String errorMessage;
}
