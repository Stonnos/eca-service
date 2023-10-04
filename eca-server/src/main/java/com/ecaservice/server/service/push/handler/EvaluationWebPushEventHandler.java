package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.EvaluationWebPushEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Classifier evaluation web push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class EvaluationWebPushEventHandler extends AbstractUserPushNotificationEventHandler<EvaluationWebPushEvent> {

    private final EvaluationPushMessageHandler evaluationPushMessageHandler;

    /**
     * Constructor with parameters.
     *
     * @param evaluationPushMessageHandler - evaluation push message handler
     */
    public EvaluationWebPushEventHandler(EvaluationPushMessageHandler evaluationPushMessageHandler) {
        super(EvaluationWebPushEvent.class);
        this.evaluationPushMessageHandler = evaluationPushMessageHandler;
    }

    @Override
    protected String getMessageType(EvaluationWebPushEvent experimentWebPushEvent) {
        return experimentWebPushEvent.getPushMessageParams().getMessageType();
    }

    @Override
    protected String getMessageText(EvaluationWebPushEvent experimentWebPushEvent) {
        return evaluationPushMessageHandler.processMessageText(experimentWebPushEvent.getPushMessageParams(),
                experimentWebPushEvent.getEvaluationLog());
    }

    @Override
    protected Map<String, String> createAdditionalProperties(EvaluationWebPushEvent experimentWebPushEvent) {
        return evaluationPushMessageHandler.processAdditionalProperties(experimentWebPushEvent.getPushMessageParams(),
                experimentWebPushEvent.getEvaluationLog());
    }

    @Override
    protected List<String> getReceivers(EvaluationWebPushEvent experimentWebPushEvent) {
        return Collections.singletonList(experimentWebPushEvent.getEvaluationLog().getCreatedBy());
    }
}
