package com.ecaservice.server.model.evaluation;

import com.ecaservice.server.model.entity.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * Evaluation web request data model.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public class EvaluationWebRequestDataModel extends AbstractClassifierRequestDataModel {

    /**
     * User name
     */
    private String createdBy;

    public EvaluationWebRequestDataModel() {
        super(Channel.WEB);
    }

    @Override
    public void visit(EvaluationRequestDataVisitor visitor) {
        visitor.visit(this);
    }
}
