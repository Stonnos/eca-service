package com.ecaservice.external.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Tolerate;

import static com.ecaservice.external.api.dto.Constraints.MAX_LENGTH_255;

/**
 * Experiment response dto.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@Schema(description = "Experiment response model")
public class ExperimentResponseDto extends SimpleEvaluationResponseDto {

    @Tolerate
    public ExperimentResponseDto() {
        //default constructor
    }

    /**
     * Experiment model url
     */
    @Schema(description = "Experiment model url", maxLength = MAX_LENGTH_255)
    private String experimentModelUrl;
}
