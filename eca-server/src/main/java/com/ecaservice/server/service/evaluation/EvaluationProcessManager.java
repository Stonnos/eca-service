package com.ecaservice.server.service.evaluation;

import com.ecaservice.server.bpm.model.EvaluationRequestModel;
import com.ecaservice.server.bpm.service.ProcessManager;
import com.ecaservice.server.config.ProcessConfig;
import com.ecaservice.server.model.entity.EvaluationLog;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_LOG_ID;
import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_DATA;

/**
 * Evaluation process manager.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationProcessManager {

    private final ProcessManager processManager;
    private final ProcessConfig processConfig;

    /**
     * Processes evaluation request.
     *
     * @param evaluationLog - evaluation log
     */
    @NewSpan
    public void processEvaluationRequest(EvaluationLog evaluationLog) {
        putMdc(TX_ID, evaluationLog.getRequestId());
        log.info("Starting to process evaluation request [{}] business process", evaluationLog.getRequestId());
        Map<String, Object> variables = Collections.singletonMap(EVALUATION_LOG_ID, evaluationLog.getId());
        processManager.startProcess(processConfig.getProcessEvaluationId(), evaluationLog.getRequestId(), variables);
        log.info("Evaluation request [{}] business process has been finished", evaluationLog.getRequestId());
    }

    /**
     * Creates and processed evaluation request.
     *
     * @param evaluationRequestModel - evaluation request dto
     */
    @NewSpan
    public void createAndProcessEvaluationRequest(EvaluationRequestModel evaluationRequestModel) {
        putMdc(TX_ID, evaluationRequestModel.getRequestId());
        log.info("Starting create and process evaluation [{}] request business process",
                evaluationRequestModel.getRequestId());
        Map<String, Object> variables = Collections.singletonMap(EVALUATION_REQUEST_DATA, evaluationRequestModel);
        processManager.startProcess(processConfig.getCreateEvaluationRequestProcessId(),
                evaluationRequestModel.getRequestId(), variables);
        log.info("Create and process evaluation [{}] business process has been finished",
                evaluationRequestModel.getRequestId());
    }
}
