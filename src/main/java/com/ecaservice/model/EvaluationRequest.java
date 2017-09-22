package com.ecaservice.model;

import com.ecaservice.model.entity.EvaluationMethod;
import com.ecaservice.model.InputData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Evaluation request model.
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {

    private String ipAddress;

    private Date requestDate;

    private InputData inputData;

    private EvaluationMethod evaluationMethod;

    private Integer numFolds;

    private Integer numTests;
}
