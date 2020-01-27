package com.ecaservice.event.listener;

import com.ecaservice.event.model.ExperimentResultsSendingEvent;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.experiment.ExperimentResultsLockService;
import com.ecaservice.service.experiment.ExperimentService;
import eca.converters.model.ExperimentHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ecaservice.config.EcaServiceConfiguration.ECA_THREAD_POOL_TASK_EXECUTOR;

/**
 * Event listener for experiment results sending.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentResultsSendingEventListener {

    private final ExperimentService experimentService;
    private final ErsService ersService;
    private final ExperimentResultsLockService lockService;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;

    /**
     * Handles event to sent experiment results to ERS.
     *
     * @param event - experiment results sending event
     */
    @Async(ECA_THREAD_POOL_TASK_EXECUTOR)
    @EventListener
    public void handleExperimentResultsSendingEvent(ExperimentResultsSendingEvent event) {
        Experiment experiment = event.getExperiment();
        try {
            List<ExperimentResultsEntity> experimentResultsEntityList =
                    experimentResultsEntityRepository.findExperimentsResultsToErsSent(experiment);
            ExperimentHistory experimentHistory = experimentService.getExperimentHistory(experiment);
            experimentResultsEntityList.forEach(
                    experimentResultsEntity -> ersService.sentExperimentResults(experimentResultsEntity,
                            experimentHistory, event.getExperimentResultsRequestSource()));
        } catch (Exception ex) {
            log.error("There was an error while experiment history manually sending: {}", ex.getMessage());
        } finally {
            lockService.unlock(experiment.getUuid());
        }
    }
}
