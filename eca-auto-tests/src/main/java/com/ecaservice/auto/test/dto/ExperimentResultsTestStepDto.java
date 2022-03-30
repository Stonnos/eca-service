package com.ecaservice.auto.test.dto;

import com.ecaservice.auto.test.model.evaluation.EvaluationResultsDetailsMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Experiment results test step dto.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExperimentResultsTestStepDto extends BaseTestStepDto {

    /**
     * Experiment results details matches
     */
    @Schema(description = "Experiment results details matches")
    private List<EvaluationResultsDetailsMatch> experimentResultDetails;
}
