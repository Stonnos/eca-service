package com.ecaservice.server.model.experiment;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Experiment message request data model (from MQ).
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExperimentMessageRequestData extends AbstractExperimentRequestData {

    /**
     * Reply to queue
     */
    private String replyTo;

    /**
     * MQ message correlation id
     */
    private String correlationId;

    @Override
    public void visit(ExperimentRequestDataVisitor visitor) {
        visitor.visit(this);
    }
}
