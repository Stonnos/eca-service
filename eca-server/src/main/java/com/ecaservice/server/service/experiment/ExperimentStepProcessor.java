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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentStepProcessor {

    private final ExperimentStepRepository experimentStepRepository;
    private final List<AbstractExperimentStepHandler> experimentStepHandlers;

    public boolean allStepsCompleted(Experiment experiment) {
        long notCompletedCount =
                experimentStepRepository.countByStatusNotIn(Collections.singletonList(ExperimentStepStatus.COMPLETED));
        log.info("Not completed steps for experiment [{}]: {}", experiment.getRequestId(), notCompletedCount);
        return notCompletedCount == 0L;
    }

    public ExperimentContext processExperimentSteps(Experiment experiment) throws Exception {
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
        log.info("Experiment [{}] has been successfully finished!", experiment.getRequestId());
        log.info(stopWatch.prettyPrint());
        return experimentContext;
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
