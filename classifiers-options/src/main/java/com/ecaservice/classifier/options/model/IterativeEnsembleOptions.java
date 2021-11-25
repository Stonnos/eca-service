package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.ecaservice.classifier.options.model.Constraints.VALUE_1;
import static com.ecaservice.classifier.options.model.Constraints.VALUE_10;

/**
 * Iterative ensemble classifier options model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class IterativeEnsembleOptions extends ClassifierOptions {

    /**
     * Iterations number
     */
    @Min(VALUE_1)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Iterations number")
    private Integer numIterations;

    /**
     * Threads number
     */
    @Min(VALUE_1)
    @Max(VALUE_10)
    @Schema(description = "Threads number")
    private Integer numThreads;

    /**
     * Seed value for random generator
     */
    @Min(Integer.MIN_VALUE)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Seed value for random generator")
    private Integer seed;
}
