package com.ecaservice.dto;

import com.ecaservice.model.EvaluationMethod;
import eca.model.InputData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {

    private String ipAddress;

    private LocalDateTime requestDate;

    private InputData inputData;

    private EvaluationMethod evaluationMethod;

    private Integer numFolds;

    private Integer numTests;
}
