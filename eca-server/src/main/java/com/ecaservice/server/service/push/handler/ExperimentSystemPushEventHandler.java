package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.ExperimentSystemPushEvent;
import com.ecaservice.server.service.push.EvaluationPushPropertiesHandler;
import com.ecaservice.server.service.push.PushMessageProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Experiment web push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentSystemPushEventHandler extends AbstractSystemPushEventHandler<ExperimentSystemPushEvent> {

    private final EvaluationPushPropertiesHandler evaluationPushPropertiesHandler;
    private final PushMessageProcessor pushMessageProcessor;

    /**
     * Constructor with parameters.
     *
     * @param evaluationPushPropertiesHandler - evaluation push properties handler
     * @param pushMessageProcessor  - evaluation push message handler
     */
    public ExperimentSystemPushEventHandler(EvaluationPushPropertiesHandler evaluationPushPropertiesHandler,
                                            PushMessageProcessor pushMessageProcessor) {
        super(ExperimentSystemPushEvent.class);
        this.evaluationPushPropertiesHandler = evaluationPushPropertiesHandler;
        this.pushMessageProcessor = pushMessageProcessor;
    }

    @Override
    protected String getMessageType(ExperimentSystemPushEvent experimentSystemPushEvent) {
        return experimentSystemPushEvent.getPushMessageParams().getMessageType();
    }

    @Override
    protected String getMessageText(ExperimentSystemPushEvent experimentSystemPushEvent) {
        return pushMessageProcessor.processMessageText(experimentSystemPushEvent.getPushMessageParams(),
                experimentSystemPushEvent.getExperiment());
    }

    @Override
    protected Map<String, String> createAdditionalProperties(ExperimentSystemPushEvent experimentSystemPushEvent) {
        return evaluationPushPropertiesHandler.processAdditionalProperties(
                experimentSystemPushEvent.getPushMessageParams(), experimentSystemPushEvent.getExperiment());
    }
}
