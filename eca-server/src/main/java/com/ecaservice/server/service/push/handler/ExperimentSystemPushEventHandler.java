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

    private final ExperimentPushMessageHandler experimentPushMessageHandler;

    /**
     * Constructor with parameters.
     *
     * @param experimentPushMessageHandler - experiment push message handler
     */
    public ExperimentSystemPushEventHandler(ExperimentPushMessageHandler experimentPushMessageHandler) {
        super(ExperimentSystemPushEvent.class);
        this.experimentPushMessageHandler = experimentPushMessageHandler;
    }

    @Override
    protected String getMessageType(ExperimentSystemPushEvent experimentSystemPushEvent) {
        return experimentSystemPushEvent.getPushMessageParams().getMessageType();
    }

    @Override
    protected String getMessageText(ExperimentSystemPushEvent experimentSystemPushEvent) {
        return experimentPushMessageHandler.processMessageText(experimentSystemPushEvent.getPushMessageParams(),
                experimentSystemPushEvent.getExperiment());
    }

    @Override
    protected Map<String, String> createAdditionalProperties(ExperimentSystemPushEvent experimentSystemPushEvent) {
        return experimentPushMessageHandler.processAdditionalProperties(
                experimentSystemPushEvent.getPushMessageParams(), experimentSystemPushEvent.getExperiment());
    }
}
