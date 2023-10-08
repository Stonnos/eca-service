package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.ExperimentRequestModel;
import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.mapping.ExperimentMapper;
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
    private final ExperimentMapper experimentMapper;

    /**
     * Constructor with parameters.
     *
     * @param experimentService - experiment service
     * @param experimentMapper - experiment mapper
     */
    public CreateExperimentRequestTaskHandler(ExperimentService experimentService,
                                              ExperimentMapper experimentMapper) {
        super(TaskType.CREATE_EXPERIMENT_REQUEST);
        this.experimentService = experimentService;
        this.experimentMapper = experimentMapper;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to create experiment [{}] for process", execution.getProcessBusinessKey());
        var experimentRequestModel =
                getVariable(execution, EVALUATION_REQUEST_DATA, ExperimentRequestModel.class);
        var experimentRequestData = experimentMapper.map(experimentRequestModel);
        var experiment = experimentService.createExperiment(experimentRequestData);
        execution.setVariable(EXPERIMENT_ID, experiment.getId());
        log.info("Experiment [{}] has been created for process", execution.getProcessBusinessKey());
    }
}
