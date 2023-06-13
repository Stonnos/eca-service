package com.ecaservice.server.model;

import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import com.ecaservice.server.model.entity.ErsRequest;
import eca.core.evaluation.EvaluationResults;
import lombok.Builder;
import lombok.Data;

/**
 * Ers evaluation request data.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class ErsEvaluationRequestData {

    /**
     * Ers request entity
     */
    private ErsRequest ersRequest;

    /**
     * Evaluation entity
     */
    private AbstractEvaluationEntity evaluationEntity;

    /**
     * Evaluation results
     */
    private EvaluationResults evaluationResults;
}
