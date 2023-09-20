package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.ExperimentWebPushEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_STATUS_MESSAGE_TYPE;

/**
 * Experiment web push event handler.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentWebPushEventHandler extends AbstractUserPushNotificationEventHandler<ExperimentWebPushEvent> {

    private final ExperimentPushMessageHandler experimentPushMessageHandler;

    /**
     * Constructor with parameters.
     *
     * @param experimentPushMessageHandler - experiment push message handler
     */
    public ExperimentWebPushEventHandler(ExperimentPushMessageHandler experimentPushMessageHandler) {
        super(ExperimentWebPushEvent.class);
        this.experimentPushMessageHandler = experimentPushMessageHandler;
    }

    @Override
    protected String getMessageType(ExperimentWebPushEvent experimentWebPushEvent) {
        return EXPERIMENT_STATUS_MESSAGE_TYPE;
    }

    @Override
    protected String getMessageText(ExperimentWebPushEvent experimentWebPushEvent) {
        return experimentPushMessageHandler.processMessageText(experimentWebPushEvent.getPushMessageParams(),
                experimentWebPushEvent.getExperiment());
    }

    @Override
    protected Map<String, String> createAdditionalProperties(ExperimentWebPushEvent experimentWebPushEvent) {
        return experimentPushMessageHandler.processAdditionalProperties(experimentWebPushEvent.getPushMessageParams(),
                experimentWebPushEvent.getExperiment());
    }

    @Override
    protected List<String> getReceivers(ExperimentWebPushEvent experimentWebPushEvent) {
        return Collections.singletonList(experimentWebPushEvent.getExperiment().getCreatedBy());
    }
}
