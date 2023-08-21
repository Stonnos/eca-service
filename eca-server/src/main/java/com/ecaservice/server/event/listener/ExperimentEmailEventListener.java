package com.ecaservice.server.event.listener;

import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.service.experiment.visitor.ExperimentEmailEventVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Experiment notification event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentEmailEventListener {

    private final ExperimentEmailEventVisitor experimentEmailEventVisitor;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Handles event to sent email about experiment status change.
     *
     * @param experimentEmailEvent - experiment email event
     */
    @EventListener
    public void handleExperimentEmailEvent(ExperimentEmailEvent experimentEmailEvent) {
        Experiment experiment = experimentEmailEvent.getExperiment();
        log.info("Handles experiment [{}] email event from source [{}]", experiment.getRequestId(),
                experimentEmailEvent.getSource().getClass().getSimpleName());
        if (StringUtils.isEmpty(experiment.getEmail())) {
            log.warn("Experiment [{}] email is not specified. Skipped email sending.", experiment.getRequestId());
        } else {
            var emailEvent = experiment.getRequestStatus().handle(experimentEmailEventVisitor, experiment);
            applicationEventPublisher.publishEvent(emailEvent);
        }
    }
}
