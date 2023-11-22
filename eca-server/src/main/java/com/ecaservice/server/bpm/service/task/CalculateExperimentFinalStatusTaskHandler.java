package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.exception.experiment.ExperimentException;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentStepRepository;
import com.ecaservice.server.service.experiment.ExperimentDataService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_STATUS;
import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT_ID;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Gets experiment process status task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class CalculateExperimentFinalStatusTaskHandler extends AbstractTaskHandler {

    private static final List<ExperimentStepStatus> EXPERIMENT_STEP_STATUSES_TO_PROCESS =
            List.of(ExperimentStepStatus.READY, ExperimentStepStatus.IN_PROGRESS, ExperimentStepStatus.FAILED);

    private final ExperimentDataService experimentDataService;
    private final ExperimentStepRepository experimentStepRepository;

    /**
     * Constructor with parameters.
     *
     * @param experimentDataService    - experiment data service
     * @param experimentStepRepository - experiment step repository
     */
    public CalculateExperimentFinalStatusTaskHandler(ExperimentDataService experimentDataService,
                                                     ExperimentStepRepository experimentStepRepository) {
        super(TaskType.CALCULATE_EXPERIMENT_FINAL_STATUS);
        this.experimentDataService = experimentDataService;
        this.experimentStepRepository = experimentStepRepository;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to calculate experiment [{}] final status", execution.getProcessBusinessKey());
        Long id = getVariable(execution, EXPERIMENT_ID, Long.class);
        var experiment = experimentDataService.getById(id);
        RequestStatus finalStatus = calculateFinalStatus(experiment);
        execution.setVariable(EVALUATION_REQUEST_STATUS, finalStatus.name());
        log.info("Experiment [{}] final status [{}] has been calculated", execution.getProcessBusinessKey(),
                finalStatus);
    }

    private RequestStatus calculateFinalStatus(Experiment experiment) {
        var stepStatuses = experimentStepRepository.getStepStatuses(experiment);
        if (CollectionUtils.isEmpty(stepStatuses)) {
            throw new ExperimentException(
                    String.format("Got empty steps for experiment [%s]", experiment.getRequestId()));
        }
        if (stepStatuses.stream().anyMatch(EXPERIMENT_STEP_STATUSES_TO_PROCESS::contains)) {
            String error =
                    String.format("Can't calculate experiment [%s] final status. Steps contains one of %s status",
                            experiment.getRequestId(), EXPERIMENT_STEP_STATUSES_TO_PROCESS);
            throw new ExperimentException(error);
        }
        if (stepStatuses.stream().anyMatch(ExperimentStepStatus.ERROR::equals)) {
            return RequestStatus.ERROR;
        }
        if (stepStatuses.stream().anyMatch(ExperimentStepStatus.TIMEOUT::equals)) {
            return RequestStatus.TIMEOUT;
        }
        return RequestStatus.FINISHED;
    }
}
