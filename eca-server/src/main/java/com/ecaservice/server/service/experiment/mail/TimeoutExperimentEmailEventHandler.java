package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.event.model.mail.TimeoutExperimentEmailEvent;
import com.ecaservice.server.service.experiment.dictionary.TemplateVariablesDictionary;
import com.ecaservice.server.service.experiment.dictionary.Templates;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Timeout experiment email event handler.
 *
 * @author Roman Batygin
 */
@Component
public class TimeoutExperimentEmailEventHandler
        extends AbstractExperimentEmailEventHandler<TimeoutExperimentEmailEvent> {

    private final ExperimentConfig experimentConfig;

    /**
     * Constructor with parameters.
     *
     * @param experimentConfig - experiment config
     */
    public TimeoutExperimentEmailEventHandler(ExperimentConfig experimentConfig) {
        super(TimeoutExperimentEmailEvent.class, Templates.TIMEOUT_EXPERIMENT);
        this.experimentConfig = experimentConfig;
    }

    @Override
    public Map<String, String> createVariables(TimeoutExperimentEmailEvent event) {
        var variablesMap = super.createVariables(event);
        variablesMap.put(TemplateVariablesDictionary.TIMEOUT_KEY, String.valueOf(experimentConfig.getTimeout()));
        return variablesMap;
    }
}
