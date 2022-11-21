package com.ecaservice.external.api.test.dto;

import com.ecaservice.test.common.model.MatchResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Experiment request auto test dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Experiment request auto test dto model")
public class ExperimentRequestAutoTestDto extends AbstractAutoTestDto {

    /**
     * Expected models number in experiment history
     */
    @Schema(description = "Expected models number in experiment history")
    private Integer expectedNumModels;

    /**
     * Actual models number in experiment history
     */
    @Schema(description = "Actual models number in experiment history")
    private Integer actualNumModels;

    /**
     * Models number match result in experiment history
     */
    @Schema(description = "Models number match result in experiment history")
    private MatchResult numModelsMatchResult;
}
