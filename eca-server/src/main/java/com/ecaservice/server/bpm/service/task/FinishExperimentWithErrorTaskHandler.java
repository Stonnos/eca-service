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
 * Finish experiment with error task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class FinishExperimentWithErrorTaskHandler extends AbstractTaskHandler {

    private final ExperimentDataService experimentDataService;
    private final ExperimentService experimentService;

    /**
     * Constructor with parameters.
     *
     * @param experimentDataService - experiment data service
     * @param experimentService     - experiment service
     */
    public FinishExperimentWithErrorTaskHandler(ExperimentDataService experimentDataService,
                                                ExperimentService experimentService) {
        super(TaskType.FINISH_EXPERIMENT_WITH_ERROR);
        this.experimentDataService = experimentDataService;
        this.experimentService = experimentService;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to process experiment [{}] finish with error task", execution.getProcessBusinessKey());
        Long id = getVariable(execution, EXPERIMENT_ID, Long.class);
        var experiment = experimentDataService.getById(id);
        experimentService.finishExperimentWithError(experiment);
        log.info("Experiment [{}] finish with error task has been processed", execution.getProcessBusinessKey());
    }
}
