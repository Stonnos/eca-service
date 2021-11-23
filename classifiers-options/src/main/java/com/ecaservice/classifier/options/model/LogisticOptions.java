package com.ecaservice.classifier.options.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.ecaservice.classifier.options.model.Constraints.VALUE_1;

/**
 * Logistic regression input options model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Logistic regression classifier options")
@EqualsAndHashCode(callSuper = true)
public class LogisticOptions extends ClassifierOptions {

    /**
     * Maximum iterations number for optimization method
     */
    @Min(VALUE_1)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Maximum iterations number for optimization method")
    private Integer maxIts;

    /**
     * Is use conjugate gradient descent method?
     */
    @Schema(description = "Use conjugate gradient descent method")
    private Boolean useConjugateGradientDescent;
}
