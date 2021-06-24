package com.ecaservice.report.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
     * Training data name
     */
    private String relationName;
}
