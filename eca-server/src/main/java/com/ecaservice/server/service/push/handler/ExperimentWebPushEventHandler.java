package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.ExperimentWebPushEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Experiment web push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentWebPushEventHandler extends AbstractUserPushNotificationEventHandler<ExperimentWebPushEvent> {

    private final EvaluationPushMessageHandler evaluationPushMessageHandler;

    /**
     * Constructor with parameters.
     *
     * @param evaluationPushMessageHandler - evaluation push message handler
     */
    public ExperimentWebPushEventHandler(EvaluationPushMessageHandler evaluationPushMessageHandler) {
        super(ExperimentWebPushEvent.class);
        this.evaluationPushMessageHandler = evaluationPushMessageHandler;
    }

    @Override
    protected String getMessageType(ExperimentWebPushEvent experimentWebPushEvent) {
        return experimentWebPushEvent.getPushMessageParams().getMessageType();
    }

    @Override
    protected String getMessageText(ExperimentWebPushEvent experimentWebPushEvent) {
        return evaluationPushMessageHandler.processMessageText(experimentWebPushEvent.getPushMessageParams(),
                experimentWebPushEvent.getExperiment());
    }

    @Override
    protected Map<String, String> createAdditionalProperties(ExperimentWebPushEvent experimentWebPushEvent) {
        return evaluationPushMessageHandler.processAdditionalProperties(experimentWebPushEvent.getPushMessageParams(),
                experimentWebPushEvent.getExperiment());
    }

    @Override
    protected List<String> getReceivers(ExperimentWebPushEvent experimentWebPushEvent) {
        return Collections.singletonList(experimentWebPushEvent.getExperiment().getCreatedBy());
    }
}
