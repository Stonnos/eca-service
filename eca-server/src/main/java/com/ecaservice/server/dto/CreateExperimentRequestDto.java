package com.ecaservice.server.dto;

import com.ecaservice.base.model.ExperimentType;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.io.Serializable;

import static com.ecaservice.ers.dto.Constraints.MIN_1;
import static com.ecaservice.ers.dto.Constraints.UUID_MAX_SIZE;
import static com.ecaservice.ers.dto.Constraints.UUID_PATTERN;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_MAX_LENGTH;

/**
 * Experiment request dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Experiment request")
public class CreateExperimentRequestDto implements Serializable {

    /**
     * Instances uuid
     */
    @NotBlank
    @Pattern(regexp = UUID_PATTERN)
    @Size(min = MIN_1, max = UUID_MAX_SIZE)
    @Schema(description = "Instances uuid", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "1d2de514-3a87-4620-9b97-c260e24340de",
            maxLength = UUID_MAX_LENGTH)
    private String instancesUuid;

    /**
     * Experiment type
     */
    @NotNull
    @Schema(description = "Experiment type", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = MAX_LENGTH_255)
    private ExperimentType experimentType;

    /**
     * Evaluation method
     */
    @NotNull
    @Schema(description = "Evaluation method", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = MAX_LENGTH_255)
    private EvaluationMethod evaluationMethod;
}
