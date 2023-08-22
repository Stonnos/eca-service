package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.service.experiment.ExperimentDataService;
import com.ecaservice.server.service.experiment.ExperimentService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT_ID;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Start experiment task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class StartExperimentTaskHandler extends AbstractTaskHandler {

    private final ExperimentDataService experimentDataService;
    private final ExperimentService experimentService;

    /**
     * Constructor with parameters.
     *
     * @param experimentDataService - experiment data service
     * @param experimentService     - experiment service
     */
    public StartExperimentTaskHandler(ExperimentDataService experimentDataService,
                                      ExperimentService experimentService) {
        super(TaskType.START_EXPERIMENT);
        this.experimentDataService = experimentDataService;
        this.experimentService = experimentService;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to process experiment process [{}] start task", execution.getProcessBusinessKey());
        Long id = getVariable(execution, EXPERIMENT_ID, Long.class);
        var experiment = experimentDataService.getById(id);
        experimentService.startExperiment(experiment);
        log.info("Experiment process [{}] start task has been processed", execution.getProcessBusinessKey());
    }
}
