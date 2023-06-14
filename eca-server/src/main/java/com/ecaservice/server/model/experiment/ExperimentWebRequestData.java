package com.ecaservice.server.model.experiment;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Experiment web request data model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExperimentWebRequestData  extends AbstractExperimentRequestData {

    /**
     * User name
     */
    private String createdBy;

    @Override
    public void visit(ExperimentRequestDataVisitor visitor) {
        visitor.visit(this);
    }
}
