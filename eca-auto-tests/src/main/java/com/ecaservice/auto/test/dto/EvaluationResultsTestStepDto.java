package com.ecaservice.auto.test.dto;

import com.ecaservice.auto.test.model.evaluation.EvaluationResultsDetailsMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Evaluation results test step dto.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EvaluationResultsTestStepDto extends BaseTestStepDto {

    /**
     * Evaluation results details match
     */
    @Schema(description = "Evaluation results details match")
    private EvaluationResultsDetailsMatch evaluationResultsDetails;
}
