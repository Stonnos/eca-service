package com.ecaservice.server.dto;

import com.ecaservice.base.model.ExperimentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Experiment request dto.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Experiment request")
public class CreateExperimentRequestDto extends AbstractEvaluationRequestDto {

    /**
     * Experiment type
     */
    @NotNull
    @Schema(description = "Experiment type", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = MAX_LENGTH_255)
    private ExperimentType experimentType;
}
