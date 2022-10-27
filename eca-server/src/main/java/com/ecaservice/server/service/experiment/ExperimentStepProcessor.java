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

    private final ExperimentStepRepository experimentStepRepository;
    private final List<AbstractExperimentStepHandler> experimentStepHandlers;

    /**
     * Processes experiment steps.
     *
     * @param experiment - experiment entity
     * @return experiment context
     * @throws Exception in case of error
     */
    public ExperimentContext processExperimentSteps(Experiment experiment) throws Exception {
        log.info("Starting to process experiment [{}] steps", experiment.getRequestId());
        var steps = experimentStepRepository.findByExperimentAndStatusInOrderByOrder(experiment,
                List.of(ExperimentStepStatus.READY, ExperimentStepStatus.FAILED));
        var stepNames = steps.stream()
                .map(ExperimentStepEntity::getStep)
                .collect(Collectors.toList());
        log.info("Got experiment [{}] steps {} to process", experiment.getRequestId(), stepNames);
        StopWatch stopWatch = new StopWatch(String.format("Timer for experiment [%s]", experiment.getRequestId()));
        ExperimentContext experimentContext = ExperimentContext.builder()
                .experiment(experiment)
                .stopWatch(stopWatch)
                .build();
        processSteps(steps, experimentContext);
        log.info("Experiment [{}] steps has been processed", experiment.getRequestId());
        log.info(stopWatch.prettyPrint());
        return experimentContext;
    }

    private void processSteps(List<ExperimentStepEntity> steps, ExperimentContext experimentContext) throws Exception {
        var experiment = experimentContext.getExperiment();
        for (var experimentStepEntity : steps) {
            log.info("Starting to process experiment [{}] step [{}]", experiment.getRequestId(),
                    experimentStepEntity.getStep());
            processStep(experimentContext, experimentStepEntity);
            if (!ExperimentStepStatus.COMPLETED.equals(experimentStepEntity.getStatus())) {
                log.warn("Experiment [{}] step [{}] has been terminated with status [{}].", experiment.getRequestId(),
                        experimentStepEntity.getStep(), experimentStepEntity.getStatus());
                break;
            } else {
                log.info("Experiment [{}] step [{}] has been completed.", experiment.getRequestId(),
                        experimentStepEntity.getStep());
            }
        }
    }

    private void processStep(ExperimentContext experimentContext,
                             ExperimentStepEntity experimentStepEntity) throws Exception {
        var stepHandler = experimentStepHandlers.stream()
                .filter(handler -> handler.getStep().equals(experimentStepEntity.getStep()))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(
                                String.format("Can't handle step [%s]", experimentStepEntity.getStatus())));
        stepHandler.handle(experimentContext, experimentStepEntity);
    }
}
