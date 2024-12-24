package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.ExperimentModel;
import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.repository.ExperimentStepRepository;
import com.ecaservice.server.service.experiment.ExperimentDataService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Gets experiment process status task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class GetExperimentProcessStatusTaskHandler extends AbstractTaskHandler {

    private static final List<ExperimentStepStatus> EXPERIMENT_STEP_STATUSES_TO_PROCESS =
            List.of(ExperimentStepStatus.READY, ExperimentStepStatus.IN_PROGRESS, ExperimentStepStatus.FAILED);

    private static final List<ExperimentStepStatus> ERROR_STEP_STATUSES =
            List.of(ExperimentStepStatus.ERROR, ExperimentStepStatus.TIMEOUT);

    private final ExperimentDataService experimentDataService;
    private final ExperimentStepRepository experimentStepRepository;

    /**
     * Constructor with parameters.
     *
     * @param experimentDataService    - experiment data service
     * @param experimentStepRepository - experiment step repository
     */
    public GetExperimentProcessStatusTaskHandler(ExperimentDataService experimentDataService,
                                                 ExperimentStepRepository experimentStepRepository) {
        super(TaskType.GET_EXPERIMENT_PROCESS_STATUS);
        this.experimentDataService = experimentDataService;
        this.experimentStepRepository = experimentStepRepository;
    }

    @Override
    public void handle(DelegateExecution execution) {
        var experimentModel = getVariable(execution, EXPERIMENT, ExperimentModel.class);
        log.info("Gets experiment [{}] steps count to process", experimentModel.getRequestId());
        var experiment = experimentDataService.getById(experimentModel.getId());
        long stepsCountToProcess = getStepsCountToProcess(experiment);
        experimentModel.setStepsCountToProcess(stepsCountToProcess);
        execution.setVariable(EXPERIMENT, experimentModel);
        log.info("Got experiment [{}] steps count to process: [{}]", experimentModel.getRequestId(),
                experimentModel.getStepsCountToProcess());
    }

    private long getStepsCountToProcess(Experiment experiment) {
        var stepStatuses = experimentStepRepository.getStepStatuses(experiment);
        if (stepStatuses.stream().anyMatch(ERROR_STEP_STATUSES::contains)) {
            return 0L;
        } else {
            return stepStatuses.stream()
                    .filter(EXPERIMENT_STEP_STATUSES_TO_PROCESS::contains)
                    .count();
        }
    }
}
