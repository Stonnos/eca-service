package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.ESTIMATED_TIME_LEFT_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_100_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Experiment progress dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Experiment progress model")
public class ExperimentProgressDto {

    /**
     * Is experiment processing finished?
     */
    @Schema(description = "Is experiment processing finished?")
    private boolean finished;

    /**
     * Experiment progress bar value
     */
    @Schema(description = "Experiment progress bar value", example = "85", minimum = ZERO_VALUE_STRING,
            maximum = VALUE_100_STRING)
    private Integer progress;

    /**
     * Estimated time left
     */
    @Schema(description = "Estimated time left", example = "00:01:24", maxLength = ESTIMATED_TIME_LEFT_MAX_LENGTH)
    private String estimatedTimeLeft;
}
