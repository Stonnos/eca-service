package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

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
    @Schema(description = "Experiment results id")
    private Long id;

    /**
     * Classifier info
     */
    @Schema(description = "Classifier info")
    private ClassifierInfoDto classifierInfo;

    /**
     * Experiment results index
     */
    @Schema(description = "Results index")
    private Integer resultsIndex;

    /**
     * Correctly classified percentage
     */
    @Schema(description = "Correctly classified percentage")
    private BigDecimal pctCorrect;

    /**
     * Is experiment results sent to ERS?
     */
    @Schema(description = "Is experiment results sent to ERS?")
    private boolean sent;
}
