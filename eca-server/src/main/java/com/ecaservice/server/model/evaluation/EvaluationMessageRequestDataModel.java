package com.ecaservice.server.model.evaluation;

/**
 * Evaluation request data model.
 *
 * @author Roman Batygin
 */
public class EvaluationMessageRequestDataModel extends AbstractEvaluationRequestDataModel {

    @Override
    public void visit(EvaluationRequestDataVisitor visitor) {
        visitor.visit(this);
    }
}
