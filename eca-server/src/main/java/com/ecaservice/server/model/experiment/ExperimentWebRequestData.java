package com.ecaservice.server.model.experiment;

import com.ecaservice.server.model.entity.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * Experiment web request data model.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
public class ExperimentWebRequestData extends AbstractExperimentRequestData {

    /**
     * User name
     */
    private String createdBy;

    public ExperimentWebRequestData() {
        super(Channel.WEB);
    }

    @Override
    public void visit(ExperimentRequestDataVisitor visitor) {
        visitor.visit(this);
    }
}
