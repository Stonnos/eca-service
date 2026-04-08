package com.ecaservice.server.report.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Evaluation log report model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EvaluationLogBean extends EvaluationBean {

    /**
     * Classifier name
     */
    private String classifierName;

    /**
     * Classifier options
     */
    private String classifierOptions;

    /**
     * Correctly classified percentage
     */
    private BigDecimal pctCorrect;
}
