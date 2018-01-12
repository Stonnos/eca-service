package com.ecaservice.model.options;

import lombok.Data;

/**
 * Logistic regression input options model.
 *
 * @author Roman Batygin
 */
@Data
public class LogisticOptions extends ClassifierOptions {

    private Integer maxIts;

    private Boolean useConjugateGradientDescent;
}
