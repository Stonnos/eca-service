package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_100_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Experiment results dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Experiment results model")
public class ExperimentResultsDto {

    /**
     * Experiment results id
     */
    @Schema(description = "Experiment results id", example = "1", minimum = VALUE_1_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Classifier info
     */
    @Schema(description = "Classifier info")
    private ClassifierInfoDto classifierInfo;

    /**
     * Experiment results index
     */
    @Schema(description = "Results index", example = "0", minimum = ZERO_VALUE_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private Integer resultsIndex;

    /**
     * Correctly classified percentage
     */
    @Schema(description = "Correctly classified percentage", example = "99", minimum = ZERO_VALUE_STRING,
            maximum = VALUE_100_STRING)
    private BigDecimal pctCorrect;

    /**
     * Is experiment results sent to ERS?
     */
    @Schema(description = "Is experiment results sent to ERS?")
    private boolean sent;
}
