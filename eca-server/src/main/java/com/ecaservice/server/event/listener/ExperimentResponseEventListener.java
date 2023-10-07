package com.ecaservice.server.event.listener;

import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.server.event.model.ExperimentResponseEvent;
import com.ecaservice.server.mapping.EcaResponseMapper;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.service.EcaResponseSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Experiment response event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentResponseEventListener {

    private final EcaResponseSender ecaResponseSender;
    private final EcaResponseMapper ecaResponseMapper;

    /**
     * Handles event to sent experiment response to MQ.
     *
     * @param experimentResponseEvent - experiment response event
     */
    @EventListener
    public void handleExperimentResponseEvent(ExperimentResponseEvent experimentResponseEvent) {
        Experiment experiment = experimentResponseEvent.getExperiment();
        log.info("Starting to sent experiment [{}] response for request status [{}]", experiment.getRequestId(),
                experiment.getRequestStatus());
        ExperimentResponse experimentResponse = ecaResponseMapper.map(experiment);
        ecaResponseSender.sendResponse(experimentResponse, experiment.getCorrelationId(), experiment.getReplyTo());
        log.info("Experiment [{}] response for request status [{}] has been sent",
                experiment.getRequestId(), experiment.getRequestStatus());
    }
}
