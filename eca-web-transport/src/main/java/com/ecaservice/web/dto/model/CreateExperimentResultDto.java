package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;

/**
 * Create experiment result dto model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Create experiment result model")
public class CreateExperimentResultDto {

    /**
     * Experiment id
     */
    @Schema(description = "Experiment id", required = true, example = "1", minimum = VALUE_1_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Request id
     */
    @Schema(description = "Request id", required = true, example = "1d2de514-3a87-4620-9b97-c260e24340de",
            maxLength = UUID_MAX_LENGTH)
    private String requestId;
}
