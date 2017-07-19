package com.ecaservice.model;

import eca.beans.ClassifierDescriptor;
import lombok.Data;

/**
 * Implements classification results.
 * @author Roman Batygin
 */
@Data
public class ClassificationResult {

    private ClassifierDescriptor classifierDescriptor;

    private boolean success;

    private String errorMessage;

}
