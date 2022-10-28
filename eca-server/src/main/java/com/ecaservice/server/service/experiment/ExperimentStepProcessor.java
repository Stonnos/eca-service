package com.ecaservice.server.service.experiment;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.experiment.ExperimentContext;
import com.ecaservice.server.repository.ExperimentStepRepository;
import com.ecaservice.server.service.experiment.step.AbstractExperimentStepHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Experiment step processor.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentStepProcessor {

    private static final List<ExperimentStepStatus> EXPERIMENT_STEP_STATUSES_TO_PROCESS =
            List.of(ExperimentStepStatus.READY, ExperimentStepStatus.FAILED);
    private static final List<ExperimentStepStatus> EXPERIMENT_STEP_ERROR_STATUSES =
            List.of(ExperimentStepStatus.ERROR, ExperimentStepStatus.TIMEOUT);

    private final ExperimentStepRepository experimentStepRepository;
    private final List<AbstractExperimentStepHandler> experimentStepHandlers;

    /**
     * Processes experiment steps.
     *
     * @param experiment - experiment entity
     */
    public void processExperimentSteps(Experiment experiment) {
        log.info("Starting to process experiment [{}] steps", experiment.getRequestId());
        var nextSteps = getNextStepsToProcess(experiment);
        var stepNames = nextSteps.stream().map(ExperimentStepEntity::getStep).collect(Collectors.toList());
        log.info("Got experiment [{}] steps {} to process", experiment.getRequestId(), stepNames);
        StopWatch stopWatch = new StopWatch(String.format("Timer for experiment [%s]", experiment.getRequestId()));
        ExperimentContext experimentContext = ExperimentContext.builder()
                .experiment(experiment)
                .stopWatch(stopWatch)
                .build();
        processSteps(nextSteps, experimentContext);
        log.info("Experiment [{}] steps has been processed", experiment.getRequestId());
        log.info(stopWatch.prettyPrint());
    }

    private List<ExperimentStepEntity> getNextStepsToProcess(Experiment experiment) {
        return experimentStepRepository.findByExperimentAndStatusInOrderByStepOrder(experiment,
                EXPERIMENT_STEP_STATUSES_TO_PROCESS);
    }

    private void processSteps(List<ExperimentStepEntity> steps, ExperimentContext experimentContext) {
        var experiment = experimentContext.getExperiment();
        for (var experimentStepEntity : steps) {
            log.info("Starting to process experiment [{}] step [{}]", experiment.getRequestId(),
                    experimentStepEntity.getStep());
            processStep(experimentContext, experimentStepEntity);
            if (!canHandleNext(experimentContext, experimentStepEntity)) {
                break;
            }
        }
    }

    private void processStep(ExperimentContext experimentContext,
                             ExperimentStepEntity experimentStepEntity) {
        var stepHandler = experimentStepHandlers.stream()
                .filter(handler -> handler.getStep().equals(experimentStepEntity.getStep()))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(
                                String.format("Can't handle step [%s]", experimentStepEntity.getStatus())));
        stepHandler.handle(experimentContext, experimentStepEntity);
    }

    private boolean canHandleNext(ExperimentContext experimentContext, ExperimentStepEntity experimentStepEntity) {
        Experiment experiment = experimentContext.getExperiment();
        if (!ExperimentStepStatus.COMPLETED.equals(experimentStepEntity.getStatus())) {
            log.warn("Experiment [{}] step [{}] has been completed with status [{}].", experiment.getRequestId(),
                    experimentStepEntity.getStep(), experimentStepEntity.getStatus());
            if (EXPERIMENT_STEP_ERROR_STATUSES.contains(experimentStepEntity.getStatus())) {
                experimentStepRepository.cancelSteps(experiment, LocalDateTime.now());
                log.info("All ready steps has been cancelled for experiment [{}]", experiment.getRequestId());
            }
            return false;
        } else {
            log.info("Experiment [{}] step [{}] has been successfully completed.", experiment.getRequestId(),
                    experimentStepEntity.getStep());
            return true;
        }
    }
}
