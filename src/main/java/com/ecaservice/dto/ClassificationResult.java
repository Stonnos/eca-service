package com.ecaservice.dto;

import eca.model.ClassifierDescriptor;
import lombok.Data;

/**
 * Classification results model.
 *
 * @author Roman Batygin
 */
@Data
public class ClassificationResult {

    private ClassifierDescriptor classifierDescriptor;

    private boolean success;

    private String errorMessage;

}
