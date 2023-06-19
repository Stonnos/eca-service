package com.ecaservice.server.model.evaluation;

import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.server.model.entity.RequestStatus;
import eca.core.evaluation.EvaluationResults;
import lombok.Data;

/**
 * Evaluation response data model.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationResultsDataModel {

    /**
     * Request unique identifier
     */
    private String requestId;

    /**
     * Request status
     */
    private RequestStatus status;

    /**
     * Model download url
     */
    private String modelUrl;

    /**
     * Evaluation results
     */
    private EvaluationResults evaluationResults;

    /**
     * Error code
     */
    private ErrorCode errorCode;
}
