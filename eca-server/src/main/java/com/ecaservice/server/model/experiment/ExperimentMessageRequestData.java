package com.ecaservice.server.model.experiment;

import com.ecaservice.server.model.entity.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * Experiment message request data model (from MQ).
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public class ExperimentMessageRequestData extends AbstractExperimentRequestData {

    /**
     * Reply to queue
     */
    private String replyTo;

    /**
     * MQ message correlation id
     */
    private String correlationId;

    public ExperimentMessageRequestData() {
        super(Channel.QUEUE);
    }

    @Override
    public void visit(ExperimentRequestDataVisitor visitor) {
        visitor.visit(this);
    }
}
