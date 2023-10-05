package com.ecaservice.server.service.evaluation;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.bpm.service.ProcessManager;
import com.ecaservice.server.config.ProcessConfig;
import com.ecaservice.server.dto.CreateEvaluationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
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

    private final EvaluationLogDataService evaluationLogDataService;
    private final ProcessManager processManager;
    private final ProcessConfig processConfig;

    /**
     * Processes evaluation request.
     *
     * @param id - evaluation log id
     */
    @Locked(lockName = "evaluation", key = "#id")
    public void processEvaluationRequest(Long id) {
        var evaluationLog = evaluationLogDataService.getById(id);
        putMdc(TX_ID, evaluationLog.getRequestId());
        putMdc(EV_REQUEST_ID, evaluationLog.getRequestId());
        log.info("Starting to process evaluation request [{}] business process", evaluationLog.getRequestId());
        Map<String, Object> variables = Collections.singletonMap(EVALUATION_LOG_ID, id);
        processManager.startProcess(processConfig.getProcessEvaluationId(), evaluationLog.getRequestId(), variables);
        log.info("Evaluation request [{}] business process has been finished", evaluationLog.getRequestId());
    }

    /**
     * Creates evaluation web request.
     *
     * @param requestId            - evaluation request id
     * @param evaluationRequestDto - evaluation request dto
     */
    public void createEvaluationWebRequest(String requestId, CreateEvaluationRequestDto evaluationRequestDto) {
        log.info("Starting create evaluation [{}] web request business process", requestId);
        Map<String, Object> variables = Collections.singletonMap(EVALUATION_REQUEST_DATA, evaluationRequestDto);
        processManager.startProcess(processConfig.getCreateEvaluationWebRequestProcessId(), requestId, variables);
        log.info("Create evaluation [{}] business process has been finished", requestId);
    }
}
