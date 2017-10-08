package com.ecaservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Evaluation request model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {

    /**
     * Remote ip address
     */
    private String ipAddress;

    /**
     * Input data
     */
    private InputData inputData;

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;

    /**
     * Evaluation options map
     */
    private Map<EvaluationOption, String> evaluationOptionsMap;
}
