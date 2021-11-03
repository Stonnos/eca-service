package com.ecaservice.server.model;

import com.ecaservice.base.model.ErrorCode;
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
    private ErrorCode errorCode;
}
