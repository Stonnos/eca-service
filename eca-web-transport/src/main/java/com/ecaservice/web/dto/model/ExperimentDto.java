package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_100_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Experiment dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Experiment model")
public class ExperimentDto extends AbstractEvaluationDto {

    /**
     * Experiment type
     */
    @Schema(description = "Experiment type")
    private EnumDto experimentType;

    /**
     * The best classifier correctly classified percentage
     */
    @Schema(description = "The best classifier correctly classified percentage", example = "99",
            minimum = ZERO_VALUE_STRING, maximum = VALUE_100_STRING)
    private BigDecimal maxPctCorrect;
}
