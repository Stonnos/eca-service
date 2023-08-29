package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.ExperimentModel;
import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.repository.ExperimentStepRepository;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

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

    private final ExperimentStepRepository experimentStepRepository;

    /**
     * Constructor with parameters.
     *
     * @param experimentStepRepository - experiment step repository
     */
    public GetExperimentProcessStatusTaskHandler(ExperimentStepRepository experimentStepRepository) {
        super(TaskType.GET_EXPERIMENT_PROCESS_STATUS);
        this.experimentStepRepository = experimentStepRepository;
    }

    @Override
    public void handle(DelegateExecution execution) {
        var experimentModel = getVariable(execution, EXPERIMENT, ExperimentModel.class);
        log.info("Gets experiment [{}] steps count to process", experimentModel.getRequestId());
        long stepsCountToProcess = experimentStepRepository.getExperimentStepsCountToProcess(experimentModel.getId());
        experimentModel.setStepsCountToProcess(stepsCountToProcess);
        execution.setVariable(EXPERIMENT, experimentModel);
        log.info("Got experiment [{}] steps count to process: [{}]", experimentModel.getRequestId(),
                experimentModel.getStepsCountToProcess());
    }
}
