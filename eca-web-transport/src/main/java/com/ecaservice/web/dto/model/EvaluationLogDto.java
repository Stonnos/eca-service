package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_100_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Evaluation log dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Classifier evaluation log model")
public class EvaluationLogDto extends AbstractEvaluationDto {

    /**
     * Classifier info
     */
    @Schema(description = "Classifier info")
    private ClassifierInfoDto classifierInfo;

    /**
     * Correctly classified percentage
     */
    @Schema(description = "Correctly classified percentage", example = "99", minimum = ZERO_VALUE_STRING,
            maximum = VALUE_100_STRING)
    private BigDecimal pctCorrect;
}
