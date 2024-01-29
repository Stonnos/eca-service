package com.ecaservice.server.event.model.push;

import com.ecaservice.core.push.client.event.model.AbstractUserPushNotificationEvent;
import com.ecaservice.server.model.entity.EvaluationLog;
import lombok.Getter;

/**
 * Evaluation log web push event.
 *
 * @author Roman Batygin
 */
public class EvaluationWebPushEvent extends AbstractUserPushNotificationEvent {

    /**
     * Evaluation log
     */
    @Getter
    private final EvaluationLog evaluationLog;
    @Getter
    private final PushMessageParams pushMessageParams;

    /**
     * Create a new ExperimentWebPushEvent.
     *
     * @param source            - the object on which the event initially occurred (never {@code null})
     * @param evaluationLog     - evaluation log entity
     * @param pushMessageParams - push message params
     */
    public EvaluationWebPushEvent(Object source, EvaluationLog evaluationLog, PushMessageParams pushMessageParams) {
        super(source, null);
        this.evaluationLog = evaluationLog;
        this.pushMessageParams = pushMessageParams;
    }
}
