package com.ecaservice.auto.test.dto;

import com.ecaservice.auto.test.model.evaluation.EvaluationResultsDetailsMatch;
import com.ecaservice.base.model.ExperimentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Experiment request dto.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExperimentRequestDto extends BaseEvaluationRequestDto {

    /**
     * Experiment type
     */
    @Schema(description = "Experiment type")
    private ExperimentType experimentType;

    /**
     * Experiment type description
     */
    @Schema(description = "Experiment type description")
    private String experimentTypeDescription;

    /**
     * Experiment results details matches
     */
    @Schema(description = "Experiment results details matches")
    private List<EvaluationResultsDetailsMatch> experimentResultDetails;
}
