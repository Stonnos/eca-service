package com.ecaservice.model.options;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Logistic regression input options model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LogisticOptions extends ClassifierOptions {

    /**
     * Maximum iterations number for optimization method
     */
    private Integer maxIts;

    /**
     * Is use conjugate gradient descent method?
     */
    private Boolean useConjugateGradientDescent;
}
