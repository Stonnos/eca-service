package com.ecaservice.server.bpm.model;

import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.server.model.entity.RequestStatus;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Evaluation results model for bpmn process.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationResultsModel implements Serializable {

    /**
     * Request unique identifier
     */
    private String requestId;

    /**
     * Request status
     */
    private RequestStatus requestStatus;

    /**
     * Model download url
     */
    private String modelUrl;

    /**
     * Test instances number
     */
    private Integer numTestInstances;

    /**
     * Correctly classified instances number
     */
    private Integer numCorrect;

    /**
     * Incorrectly classified instances number
     */
    private Integer numIncorrect;

    /**
     * Correctly classified percentage
     */
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    private BigDecimal meanAbsoluteError;

    /**
     * Error code
     */
    private ErrorCode errorCode;
}
