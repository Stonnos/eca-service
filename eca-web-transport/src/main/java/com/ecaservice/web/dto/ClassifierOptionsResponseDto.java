package com.ecaservice.web.dto;

import lombok.Data;

/**
 * Classifier options response dto model.
 *
 * @author Roman Batygin
 */
@Data
public class ClassifierOptionsResponseDto {

    /**
     * Classifier name
     */
    private String classifierName;

    /**
     * Classifier options config
     */
    private String options;
}
