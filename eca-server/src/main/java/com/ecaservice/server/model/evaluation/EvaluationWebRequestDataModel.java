package com.ecaservice.server.model.evaluation;

import lombok.Getter;
import lombok.Setter;

/**
 * Evaluation request data model.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public class EvaluationWebRequestDataModel extends AbstractEvaluationRequestDataModel {

    /**
     * User name
     */
    private String createdBy;

    @Override
    public void visit(EvaluationRequestDataVisitor visitor) {
        visitor.visit(this);
    }
}
