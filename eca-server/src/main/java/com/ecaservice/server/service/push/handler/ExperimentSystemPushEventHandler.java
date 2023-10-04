package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.ExperimentSystemPushEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Experiment web push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentSystemPushEventHandler extends AbstractSystemPushEventHandler<ExperimentSystemPushEvent> {

    private final EvaluationPushMessageHandler evaluationPushMessageHandler;

    /**
     * Constructor with parameters.
     *
     * @param evaluationPushMessageHandler - evaluation push message handler
     */
    public ExperimentSystemPushEventHandler(EvaluationPushMessageHandler evaluationPushMessageHandler) {
        super(ExperimentSystemPushEvent.class);
        this.evaluationPushMessageHandler = evaluationPushMessageHandler;
    }

    @Override
    protected String getMessageType(ExperimentSystemPushEvent experimentSystemPushEvent) {
        return experimentSystemPushEvent.getPushMessageParams().getMessageType();
    }

    @Override
    protected String getMessageText(ExperimentSystemPushEvent experimentSystemPushEvent) {
        return evaluationPushMessageHandler.processMessageText(experimentSystemPushEvent.getPushMessageParams(),
                experimentSystemPushEvent.getExperiment());
    }

    @Override
    protected Map<String, String> createAdditionalProperties(ExperimentSystemPushEvent experimentSystemPushEvent) {
        return evaluationPushMessageHandler.processAdditionalProperties(
                experimentSystemPushEvent.getPushMessageParams(), experimentSystemPushEvent.getExperiment());
    }
}
