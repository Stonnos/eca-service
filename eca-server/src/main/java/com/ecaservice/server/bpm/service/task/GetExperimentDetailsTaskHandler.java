package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.service.experiment.ExperimentDataService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT;
import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT_ID;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Gets experiment details task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class GetExperimentDetailsTaskHandler extends AbstractTaskHandler {

    private final ExperimentDataService experimentDataService;

    /**
     * Constructor with parameters.
     *
     * @param experimentDataService - experiment data service
     */
    public GetExperimentDetailsTaskHandler(ExperimentDataService experimentDataService) {
        super(TaskType.GET_EXPERIMENT_DETAILS);
        this.experimentDataService = experimentDataService;
    }

    @Override
    public void handle(DelegateExecution execution) {
        Long id = getVariable(execution, EXPERIMENT_ID, Long.class);
        log.info("Starting to get experiment [{}] details for process", execution.getProcessBusinessKey());
        var experimentModel = experimentDataService.getExperimentModel(id);
        execution.setVariable(EXPERIMENT, experimentModel);
        log.info("Experiment [{}] details has been fetched for process", execution.getProcessBusinessKey());
    }
}
