package com.ecaservice.external.api.dto;

import com.ecaservice.external.api.dto.annotations.DataURL;
import eca.core.evaluation.EvaluationMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

import static com.ecaservice.external.api.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.external.api.dto.Constraints.MIN_LENGTH_1;

/**
 * Experiment request dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Experiment request model")
public class ExperimentRequestDto implements Serializable {

    /**
     * Training data url
     */
    @DataURL
    @Size(min = MIN_LENGTH_1, max = MAX_LENGTH_255)
    @Schema(description = "Train data url", example = "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls",
            required = true)
    private String trainDataUrl;

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
