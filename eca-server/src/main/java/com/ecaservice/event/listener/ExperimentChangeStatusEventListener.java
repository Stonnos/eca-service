package com.ecaservice.event.listener;

import com.ecaservice.event.model.ExperimentChangeStatusEvent;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.experiment.visitor.ExperimentEmailVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener that occurs after experiment status is changed.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentChangeStatusEventListener {

    private final ExperimentEmailVisitor experimentEmailVisitor;

    /**
     * Handles event to sent email about experiment status change.
     *
     * @param changeStatusEvent - experiment change status event
     */
    @EventListener
    public void handleChangeStatusEvent(ExperimentChangeStatusEvent changeStatusEvent) {
        Experiment experiment = changeStatusEvent.getExperiment();
        experiment.getRequestStatus().handle(experimentEmailVisitor, experiment);
    }
}
