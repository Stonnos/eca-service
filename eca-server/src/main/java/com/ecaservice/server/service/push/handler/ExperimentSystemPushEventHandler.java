package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.ExperimentSystemPushEvent;
import com.ecaservice.server.service.experiment.ExperimentMessageTemplateProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_STATUS_MESSAGE_TYPE;
import static com.ecaservice.server.service.push.dictionary.PushPropertiesHelper.createExperimentProperties;

/**
 * Experiment web push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentSystemPushEventHandler extends AbstractSystemPushEventHandler<ExperimentSystemPushEvent> {

    private final ExperimentMessageTemplateProcessor experimentMessageTemplateProcessor;

    /**
     * Constructor with parameters.
     *
     * @param experimentMessageTemplateProcessor - message template processor
     */
    public ExperimentSystemPushEventHandler(ExperimentMessageTemplateProcessor experimentMessageTemplateProcessor) {
        super(ExperimentSystemPushEvent.class);
        this.experimentMessageTemplateProcessor = experimentMessageTemplateProcessor;
    }

    @Override
    protected String getMessageType() {
        return EXPERIMENT_STATUS_MESSAGE_TYPE;
    }

    @Override
    protected String getMessageText(ExperimentSystemPushEvent experimentSystemPushEvent) {
        var experiment = experimentSystemPushEvent.getExperiment();
        return experimentMessageTemplateProcessor.process(experiment);
    }

    @Override
    protected Map<String, String> createAdditionalProperties(ExperimentSystemPushEvent experimentSystemPushEvent) {
        var experiment = experimentSystemPushEvent.getExperiment();
        return createExperimentProperties(experiment);
    }
}
