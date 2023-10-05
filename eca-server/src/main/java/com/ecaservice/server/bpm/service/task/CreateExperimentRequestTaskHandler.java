package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.model.experiment.AbstractExperimentRequestData;
import com.ecaservice.server.service.experiment.ExperimentService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_DATA;
import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT_ID;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Create experiment request task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class CreateExperimentRequestTaskHandler extends AbstractTaskHandler {

    private final ExperimentService experimentService;

    /**
     * Constructor with parameters.
     *
     * @param experimentService - experiment service
     */
    public CreateExperimentRequestTaskHandler(ExperimentService experimentService) {
        super(TaskType.CREATE_EXPERIMENT_REQUEST);
        this.experimentService = experimentService;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to create experiment [{}] for process", execution.getProcessBusinessKey());
        var experimentRequestData =
                getVariable(execution, EVALUATION_REQUEST_DATA, AbstractExperimentRequestData.class);
        var experiment = experimentService.createExperiment(experimentRequestData);
        execution.setVariable(EXPERIMENT_ID, experiment.getId());
        log.info("Experiment [{}] has been created for process", execution.getProcessBusinessKey());
    }
}
