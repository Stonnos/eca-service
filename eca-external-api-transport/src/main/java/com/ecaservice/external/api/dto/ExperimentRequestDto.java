package com.ecaservice.external.api.dto;

import eca.core.evaluation.EvaluationMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

import static com.ecaservice.external.api.dto.Constraints.MAX_LENGTH_255;

/**
 * Experiment request dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Experiment request model")
public class ExperimentRequestDto extends AbstractEvaluationRequestDto {

    /**
     * Experiment type
     */
    @NotNull
    @Schema(description = "Experiment type", required = true, maxLength = MAX_LENGTH_255)
    private ExApiExperimentType experimentType;

    /**
     * Evaluation method
     */
    @NotNull
    @Schema(description = "Evaluation method", required = true, maxLength = MAX_LENGTH_255)
    private EvaluationMethod evaluationMethod;
}
