package com.ecaservice.server.event.listener;

import com.ecaservice.server.event.model.ExperimentErsReportEvent;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.service.ers.ErsService;
import com.ecaservice.server.service.experiment.ExperimentResultsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Event listener for sending experiment results to ERS.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentErsReportEventListener {

    private final ExperimentResultsService experimentResultsService;
    private final ErsService ersService;

    /**
     * Handles event to sent experiment results to ERS.
     *
     * @param experimentErsReportEvent - experiment finished event
     */
    @EventListener
    public void handleEvent(ExperimentErsReportEvent experimentErsReportEvent) {
        List<ExperimentResultsEntity> experimentResultsEntityList =
                experimentResultsService.saveExperimentResultsToErsSent(experimentErsReportEvent.getExperiment(),
                        experimentErsReportEvent.getExperimentHistory());
        experimentResultsEntityList.forEach(experimentResultsEntity -> {
            log.info("Starting to sent experiment [{}] results index [{}] to ERS",
                    experimentErsReportEvent.getExperiment().getRequestId(), experimentResultsEntity.getResultsIndex());
            ersService.sentExperimentResults(experimentResultsEntity, experimentErsReportEvent.getExperimentHistory());
        });
    }
}
