package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.core.mail.client.event.listener.handler.AbstractEmailEventHandler;
import com.ecaservice.server.event.model.mail.ExperimentEmailEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Experiment email event handler.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentEmailEventHandler extends AbstractEmailEventHandler<ExperimentEmailEvent> {

    private final ExperimentEmailTemplateVariableVisitor experimentEmailTemplateVariableVisitor;

    /**
     * Constructor without parameters.
     *
     * @param experimentEmailTemplateVariableVisitor - experiment email template variable visitor
     */
    public ExperimentEmailEventHandler(ExperimentEmailTemplateVariableVisitor experimentEmailTemplateVariableVisitor) {
        super(ExperimentEmailEvent.class);
        this.experimentEmailTemplateVariableVisitor = experimentEmailTemplateVariableVisitor;
    }

    @Override
    public String getCorrelationId(ExperimentEmailEvent event) {
        return event.getExperiment().getRequestId();
    }

    @Override
    public Map<String, String> createVariables(ExperimentEmailEvent event) {
        var experiment = event.getExperiment();
        Map<String, String> variablesMap = newHashMap();
        event.getTemplateVariables().forEach(variable -> {
            var experimentEmailTemplateVariable = ExperimentEmailTemplateVariable.valueOf(variable);
            String variableValue =
                    experimentEmailTemplateVariable.visit(experimentEmailTemplateVariableVisitor, experiment);
            variablesMap.put(experimentEmailTemplateVariable.getVariableName(), variableValue);
        });
        return variablesMap;
    }

    @Override
    public String getReceiver(ExperimentEmailEvent emailEvent) {
        return emailEvent.getExperiment().getEmail();
    }

    @Override
    public String getTemplateCode(ExperimentEmailEvent emailEvent) {
        return emailEvent.getTemplateCode();
    }
}
