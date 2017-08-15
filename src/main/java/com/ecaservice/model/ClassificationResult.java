package com.ecaservice.model;

import eca.model.ClassifierDescriptor;
import lombok.Data;

/**
 * Implements classification results.
 *
 * @author Roman Batygin
 */
@Data
public class ClassificationResult {

    private ClassifierDescriptor classifierDescriptor;

    private boolean success;

    private String errorMessage;

}
