package com.ecaservice.external.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

import static com.ecaservice.external.api.dto.Constraints.MODEL_URL_MAX_LENGTH;

/**
 * Experiment response dto.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@Schema(description = "Experiment results response model")
public class ExperimentResultsResponseDto extends SimpleEvaluationResponseDto {

    @Tolerate
    public ExperimentResultsResponseDto() {
        //default constructor
    }

    /**
     * Experiment model url
     */
    @Schema(description = "Experiment model url", maxLength = MODEL_URL_MAX_LENGTH)
    private String experimentModelUrl;
}
