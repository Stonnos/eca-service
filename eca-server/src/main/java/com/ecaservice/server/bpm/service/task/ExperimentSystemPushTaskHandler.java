package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.extract.PushMessageParamsExtractor;
import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.event.model.push.ExperimentSystemPushEvent;
import com.ecaservice.server.service.experiment.ExperimentDataService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT_ID;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Experiment system push task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ExperimentSystemPushTaskHandler extends AbstractTaskHandler {

    private final ExperimentDataService experimentDataService;
    private final ApplicationEventPublisher eventPublisher;
    private final PushMessageParamsExtractor pushMessageParamsExtractor;

    /**
     * Constructor with parameters.
     *
     * @param experimentDataService      - experiment data service
     * @param eventPublisher             - event publisher
     * @param pushMessageParamsExtractor - push message params extractor
     */
    public ExperimentSystemPushTaskHandler(ExperimentDataService experimentDataService,
                                           ApplicationEventPublisher eventPublisher,
                                           PushMessageParamsExtractor pushMessageParamsExtractor) {
        super(TaskType.SENT_EXPERIMENT_SYSTEM_PUSH);
        this.experimentDataService = experimentDataService;
        this.eventPublisher = eventPublisher;
        this.pushMessageParamsExtractor = pushMessageParamsExtractor;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to process experiment [{}] system push task", execution.getProcessBusinessKey());
        Long id = getVariable(execution, EXPERIMENT_ID, Long.class);
        var pushMessageParams = pushMessageParamsExtractor.extract(execution);
        var experiment = experimentDataService.getById(id);
        eventPublisher.publishEvent(new ExperimentSystemPushEvent(this, experiment, pushMessageParams));
        log.info("Experiment [{}] system push task has been processed", execution.getProcessBusinessKey());
    }
}
