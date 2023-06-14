package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.ExperimentWebPushEvent;
import com.ecaservice.server.service.experiment.ExperimentMessageTemplateProcessor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_STATUS_MESSAGE_TYPE;
import static com.ecaservice.server.service.push.dictionary.PushPropertiesHelper.createExperimentProperties;

/**
 * Experiment web push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentWebPushEventHandler extends AbstractUserPushNotificationEventHandler<ExperimentWebPushEvent> {

    private final ExperimentMessageTemplateProcessor experimentMessageTemplateProcessor;

    /**
     * Constructor with parameters.
     *
     * @param experimentMessageTemplateProcessor - message template processor
     */
    public ExperimentWebPushEventHandler(ExperimentMessageTemplateProcessor experimentMessageTemplateProcessor) {
        super(ExperimentWebPushEvent.class);
        this.experimentMessageTemplateProcessor = experimentMessageTemplateProcessor;
    }

    @Override
    protected String getMessageType() {
        return EXPERIMENT_STATUS_MESSAGE_TYPE;
    }

    @Override
    protected String getMessageText(ExperimentWebPushEvent experimentWebPushEvent) {
        var experiment = experimentWebPushEvent.getExperiment();
        return experimentMessageTemplateProcessor.process(experiment);
    }

    @Override
    protected Map<String, String> createAdditionalProperties(ExperimentWebPushEvent experimentWebPushEvent) {
        var experiment = experimentWebPushEvent.getExperiment();
        return createExperimentProperties(experiment);
    }

    @Override
    protected List<String> getReceivers(ExperimentWebPushEvent experimentWebPushEvent) {
        return Collections.singletonList(experimentWebPushEvent.getExperiment().getCreatedBy());
    }
}
