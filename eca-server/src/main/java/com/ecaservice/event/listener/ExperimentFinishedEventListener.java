package com.ecaservice.event.listener;

import com.ecaservice.event.model.ExperimentFinishedEvent;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.experiment.ExperimentResultsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Event listener that occurs after experiment is successfully finished.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentFinishedEventListener {

    private final ExperimentResultsService experimentResultsService;
    private final ErsService ersService;

    /**
     * Handles event to sent experiment results to ERS.
     *
     * @param experimentFinishedEvent - experiment finished event
     */
    @EventListener
    public void handleExperimentFinishedEvent(ExperimentFinishedEvent experimentFinishedEvent) {
        List<ExperimentResultsEntity> experimentResultsEntityList =
                experimentResultsService.saveExperimentResultsToErsSent(experimentFinishedEvent.getExperiment(),
                        experimentFinishedEvent.getExperimentHistory());
        experimentResultsEntityList.forEach(
                experimentResultsEntity -> ersService.sentExperimentResults(experimentResultsEntity,
                        experimentFinishedEvent.getExperimentHistory(), ExperimentResultsRequestSource.SYSTEM));
    }
}
