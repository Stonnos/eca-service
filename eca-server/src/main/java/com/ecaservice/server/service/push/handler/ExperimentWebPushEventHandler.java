package com.ecaservice.server.service.push.handler;

import com.ecaservice.core.push.client.event.listener.handler.AbstractUserPushNotificationEventHandler;
import com.ecaservice.server.event.model.push.ExperimentWebPushEvent;
import com.ecaservice.server.service.push.EvaluationPushPropertiesHandler;
import com.ecaservice.server.service.push.PushMessageProcessor;
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

    private final EvaluationPushPropertiesHandler evaluationPushPropertiesHandler;
    private final PushMessageProcessor pushMessageProcessor;

    /**
     * Constructor with parameters.
     *
     * @param evaluationPushPropertiesHandler - evaluation push properties handler
     * @param pushMessageProcessor  - evaluation push message handler
     */
    public ExperimentWebPushEventHandler(EvaluationPushPropertiesHandler evaluationPushPropertiesHandler,
                                         PushMessageProcessor pushMessageProcessor) {
        super(ExperimentWebPushEvent.class);
        this.evaluationPushPropertiesHandler = evaluationPushPropertiesHandler;
        this.pushMessageProcessor = pushMessageProcessor;
    }

    @Override
    protected String getCorrelationId(ExperimentWebPushEvent experimentWebPushEvent) {
        return experimentWebPushEvent.getExperiment().getRequestId();
    }

    @Override
    protected String getMessageType(ExperimentWebPushEvent experimentWebPushEvent) {
        return experimentWebPushEvent.getPushMessageParams().getMessageType();
    }

    @Override
    protected String getMessageText(ExperimentWebPushEvent experimentWebPushEvent) {
        return pushMessageProcessor.processMessageText(experimentWebPushEvent.getPushMessageParams(),
                experimentWebPushEvent.getExperiment());
    }

    @Override
    protected Map<String, String> createAdditionalProperties(ExperimentWebPushEvent experimentWebPushEvent) {
        return evaluationPushPropertiesHandler.processAdditionalProperties(
                experimentWebPushEvent.getPushMessageParams(), experimentWebPushEvent.getExperiment());
    }

    @Override
    protected List<String> getReceivers(ExperimentWebPushEvent experimentWebPushEvent) {
        return Collections.singletonList(experimentWebPushEvent.getExperiment().getCreatedBy());
    }
}
