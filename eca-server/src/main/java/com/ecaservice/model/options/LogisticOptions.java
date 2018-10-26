package com.ecaservice.model.options;

import lombok.Data;

/**
 * Logistic regression input options model.
 *
 * @author Roman Batygin
 */
@Data
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
