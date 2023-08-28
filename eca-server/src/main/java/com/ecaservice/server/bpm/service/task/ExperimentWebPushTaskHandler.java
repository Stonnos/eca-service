package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.event.model.push.ExperimentWebPushEvent;
import com.ecaservice.server.service.experiment.ExperimentDataService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT_ID;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Experiment web push task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ExperimentWebPushTaskHandler extends AbstractTaskHandler {

    private final ExperimentDataService experimentDataService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor with parameters.
     *
     * @param experimentDataService - experiment data service
     * @param eventPublisher        - event publisher
     */
    public ExperimentWebPushTaskHandler(ExperimentDataService experimentDataService,
                                        ApplicationEventPublisher eventPublisher) {
        super(TaskType.SENT_EXPERIMENT_WEB_PUSH);
        this.experimentDataService = experimentDataService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to process experiment [{}] web push task", execution.getProcessBusinessKey());
        Long id = getVariable(execution, EXPERIMENT_ID, Long.class);
        var experiment = experimentDataService.getById(id);
        eventPublisher.publishEvent(new ExperimentWebPushEvent(this, experiment));
        log.info("Experiment [{}] web push task has been processed", execution.getProcessBusinessKey());
    }
}
