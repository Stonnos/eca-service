package com.ecaservice.server.service.experiment.visitor;

import com.ecaservice.server.event.model.mail.AbstractExperimentEmailEvent;
import com.ecaservice.server.event.model.mail.ErrorExperimentEmailEvent;
import com.ecaservice.server.event.model.mail.FinishedExperimentEmailEvent;
import com.ecaservice.server.event.model.mail.InProgressExperimentEmailEvent;
import com.ecaservice.server.event.model.mail.NewExperimentEmailEvent;
import com.ecaservice.server.event.model.mail.TimeoutExperimentEmailEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatusVisitor;
import org.springframework.stereotype.Component;

/**
 * Experiment email event visitor.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentEmailEventVisitor implements RequestStatusVisitor<AbstractExperimentEmailEvent, Experiment> {

    @Override
    public AbstractExperimentEmailEvent caseNew(Experiment experiment) {
        return new NewExperimentEmailEvent(this, experiment);
    }

    @Override
    public AbstractExperimentEmailEvent caseFinished(Experiment experiment) {
        return new FinishedExperimentEmailEvent(this, experiment);
    }

    @Override
    public AbstractExperimentEmailEvent caseTimeout(Experiment experiment) {
        return new TimeoutExperimentEmailEvent(this, experiment);
    }

    @Override
    public AbstractExperimentEmailEvent caseError(Experiment experiment) {
        return new ErrorExperimentEmailEvent(this, experiment);
    }

    @Override
    public AbstractExperimentEmailEvent caseInProgress(Experiment experiment) {
        return new InProgressExperimentEmailEvent(this, experiment);
    }
}
