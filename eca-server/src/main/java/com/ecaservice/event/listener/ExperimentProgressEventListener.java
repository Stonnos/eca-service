package com.ecaservice.event.listener;

import com.ecaservice.event.model.ExperimentProgressEvent;
import com.ecaservice.service.experiment.ExperimentProgressService;
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
        experimentProgressService.onProgress(experimentProgressEvent.getExperiment(),
                experimentProgressEvent.getProgress());
    }
}
