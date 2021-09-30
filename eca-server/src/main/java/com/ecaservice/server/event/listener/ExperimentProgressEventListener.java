package com.ecaservice.server.event.listener;

import com.ecaservice.server.event.model.ExperimentProgressEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.service.experiment.ExperimentProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener that occurs on experiment progress event.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentProgressEventListener {

    private final ExperimentProgressService experimentProgressService;

    /**
     * Handles event to update experiment progress bar.
     *
     * @param experimentProgressEvent - experiment progress event
     */
    @EventListener
    public void handleExperimentProgressEvent(ExperimentProgressEvent experimentProgressEvent) {
        Experiment experiment = experimentProgressEvent.getExperiment();
        log.info("Experiment [{}] progress: {} %.", experiment.getRequestId(), experimentProgressEvent.getProgress());
        experimentProgressService.onProgress(experiment, experimentProgressEvent.getProgress());
    }
}
