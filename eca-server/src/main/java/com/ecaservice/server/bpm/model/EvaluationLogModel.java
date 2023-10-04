package com.ecaservice.server.bpm.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Evaluation model for bpmn process.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EvaluationLogModel extends AbstractEvaluationModel {

    /**
     * Classifier name
     */
    private String classifierName;
}
