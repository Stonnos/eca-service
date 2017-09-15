package com.ecaservice.dto;

import com.ecaservice.model.EvaluationMethod;
import eca.model.InputData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
