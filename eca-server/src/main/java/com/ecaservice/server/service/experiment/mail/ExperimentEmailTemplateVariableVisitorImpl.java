package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Experiment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Experiment template variable visitor.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class ExperimentEmailTemplateVariableVisitorImpl implements ExperimentEmailTemplateVariableVisitor {

    private final ExperimentConfig experimentConfig;

    @Override
    public String visitExperimentType(Experiment experiment) {
        return experiment.getExperimentType().getDescription();
    }

    @Override
    public String visitDownloadUrl(Experiment experiment) {
        return experiment.getExperimentDownloadUrl();
    }

    @Override
    public String visitTimeout(Experiment experiment) {
        return String.valueOf(experimentConfig.getTimeout());
    }

    @Override
    public String visitRequestId(Experiment experiment) {
        return experiment.getRequestId();
    }
}
