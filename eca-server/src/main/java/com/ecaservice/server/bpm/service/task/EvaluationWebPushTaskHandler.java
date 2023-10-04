package com.ecaservice.server.bpm.service.task;

import com.ecaservice.server.bpm.extract.PushMessageParamsExtractor;
import com.ecaservice.server.bpm.model.TaskType;
import com.ecaservice.server.event.model.push.EvaluationWebPushEvent;
import com.ecaservice.server.service.evaluation.EvaluationLogDataService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_LOG_ID;
import static com.ecaservice.server.util.CamundaUtils.getVariable;

/**
 * Classifier evaluation web push task handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationWebPushTaskHandler extends AbstractTaskHandler {

    private final EvaluationLogDataService evaluationLogDataService;
    private final ApplicationEventPublisher eventPublisher;
    private final PushMessageParamsExtractor pushMessageParamsExtractor;

    /**
     * Constructor with parameters.
     *
     * @param evaluationLogDataService   - evaluation log data service
     * @param eventPublisher             - event publisher
     * @param pushMessageParamsExtractor - push message params extractor
     */
    public EvaluationWebPushTaskHandler(EvaluationLogDataService evaluationLogDataService,
                                        ApplicationEventPublisher eventPublisher,
                                        PushMessageParamsExtractor pushMessageParamsExtractor) {
        super(TaskType.SENT_EVALUATION_WEB_PUSH);
        this.evaluationLogDataService = evaluationLogDataService;
        this.eventPublisher = eventPublisher;
        this.pushMessageParamsExtractor = pushMessageParamsExtractor;
    }

    @Override
    public void handle(DelegateExecution execution) {
        log.info("Starting to process classifier evaluation [{}] web push task", execution.getProcessBusinessKey());
        Long id = getVariable(execution, EVALUATION_LOG_ID, Long.class);
        var evaluationLog = evaluationLogDataService.getById(id);
        var pushMessageParams = pushMessageParamsExtractor.extract(execution);
        eventPublisher.publishEvent(new EvaluationWebPushEvent(this, evaluationLog, pushMessageParams));
        log.info("Classifier evaluation [{}] web push task has been processed", execution.getProcessBusinessKey());
    }
}
