package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.model.EvaluationRequestModel;
import com.ecaservice.server.bpm.model.EvaluationResultsModel;
import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.event.model.EvaluationResponseEvent;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.CORRELATION_ID;
import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_DATA;
import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_RESULTS_DATA;
import static com.ecaservice.server.bpm.CamundaVariables.REPLY_TO;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Evaluation response task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationResponseTaskHandler extends AbstractTaskHandler {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor with parameters.
     *
     * @param eventPublisher - event publisher
     */
    public EvaluationResponseTaskHandler(ApplicationEventPublisher eventPublisher) {
        super(TaskType.SENT_EVALUATION_RESPONSE);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to process evaluation [{}] response task", execution.getProcessBusinessKey());
        String correlationId = getVariable(execution, CORRELATION_ID, String.class);
        String replyTo = getVariable(execution, REPLY_TO, String.class);
        var evaluationResultsModel =
                getVariable(execution, EVALUATION_RESULTS_DATA, EvaluationResultsModel.class);
        eventPublisher.publishEvent(new EvaluationResponseEvent(this, evaluationResultsModel, correlationId, replyTo));
        log.info("Evaluation [{}] response task has been processed", execution.getProcessBusinessKey());
    }
}
