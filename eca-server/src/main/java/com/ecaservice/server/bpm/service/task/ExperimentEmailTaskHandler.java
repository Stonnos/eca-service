package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.event.model.mail.ExperimentEmailEvent;
import com.ecaservice.server.service.experiment.ExperimentDataService;
import com.ecaservice.server.service.experiment.mail.ExperimentEmailTemplateVariable;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ecaservice.server.bpm.CamundaVariables.EMAIL_TEMPLATE_CODE;
import static com.ecaservice.server.bpm.CamundaVariables.EMAIL_TEMPLATE_VARIABLES;
import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT_ID;
import static com.ecaservice.server.util.CamundaUtils.getValuesAsArray;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Experiment email task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ExperimentEmailTaskHandler extends AbstractTaskHandler {

    private final ExperimentDataService experimentDataService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor with parameters.
     *
     * @param experimentDataService - experiment data service
     * @param eventPublisher        - event publisher
     */
    public ExperimentEmailTaskHandler(ExperimentDataService experimentDataService,
                                      ApplicationEventPublisher eventPublisher) {
        super(TaskType.SENT_EXPERIMENT_EMAIL);
        this.experimentDataService = experimentDataService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to process experiment [{}] email task", execution.getProcessBusinessKey());
        Long id = getVariable(execution, EXPERIMENT_ID, Long.class);
        String templateCode = getVariable(execution, EMAIL_TEMPLATE_CODE, String.class);
        List<ExperimentEmailTemplateVariable> templateVariables = getTemplateVariables(execution);
        log.debug("Email template code [{}], variables [{}] for experiment [{}]", templateCode, templateVariables,
                execution.getProcessBusinessKey());
        var experiment = experimentDataService.getById(id);
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment, templateCode, templateVariables));
        log.info("Experiment [{}] email task has been processed", execution.getProcessBusinessKey());
    }

    private List<ExperimentEmailTemplateVariable> getTemplateVariables(DelegateExecution execution) {
        String[] templateVariables = getValuesAsArray(execution, EMAIL_TEMPLATE_VARIABLES);
        return Stream.of(templateVariables)
                .map(ExperimentEmailTemplateVariable::valueOf)
                .collect(Collectors.toList());
    }
}
