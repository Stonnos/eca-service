package com.ecaservice.server.model.evaluation;

import com.ecaservice.server.model.entity.Channel;

/**
 * Evaluation request data model.
 *
 * @author Roman Batygin
 */
public class EvaluationMessageRequestDataModel extends AbstractClassifierRequestDataModel {

    public EvaluationMessageRequestDataModel() {
        super(Channel.QUEUE);
    }

    @Override
    public void visit(EvaluationRequestDataVisitor visitor) {
        visitor.visit(this);
    }
}
