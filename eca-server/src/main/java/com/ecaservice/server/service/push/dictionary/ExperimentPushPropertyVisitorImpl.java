package com.ecaservice.server.service.push.dictionary;


import com.ecaservice.server.model.entity.Experiment;
import org.springframework.stereotype.Component;

/**
 * Experiment push property visitor.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentPushPropertyVisitorImpl implements ExperimentPushPropertyVisitor {

    @Override
    public String visitId(Experiment experiment) {
        return String.valueOf(experiment.getId());
    }

    @Override
    public String visitRequestId(Experiment experiment) {
        return experiment.getRequestId();
    }

    @Override
    public String visitRequestStatus(Experiment experiment) {
        return experiment.getRequestStatus().name();
    }
}
