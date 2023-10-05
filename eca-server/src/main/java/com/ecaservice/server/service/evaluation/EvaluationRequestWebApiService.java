package com.ecaservice.server.service.evaluation;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.server.dto.CreateEvaluationRequestDto;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.evaluation.EvaluationWebRequestDataModel;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.UserService;
import com.ecaservice.web.dto.model.CreateEvaluationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.server.config.audit.AuditCodes.CREATE_EVALUATION_REQUEST;

/**
 * Evaluation request web api service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationRequestWebApiService {

    private final UserService userService;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final EvaluationProcessManager evaluationProcessManager;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Creates evaluation request.
     *
     * @param evaluationRequestDto - evaluation request dto
     * @return evaluation response
     */
    @Audit(value = CREATE_EVALUATION_REQUEST, correlationIdKey = "#result.requestId")
    public CreateEvaluationResponseDto createEvaluationRequest(CreateEvaluationRequestDto evaluationRequestDto) {
        String requestId = UUID.randomUUID().toString();
        putMdc(TX_ID, requestId);
        putMdc(EV_REQUEST_ID, requestId);
        log.info(
                "Starting to create evaluation [{}] request for instances [{}], classifier options [{}], evaluation method [{}]",
                requestId, evaluationRequestDto.getInstancesUuid(),
                evaluationRequestDto.getClassifierOptions().getClass().getSimpleName(),
                evaluationRequestDto.getEvaluationMethod());
        var classifier = classifierOptionsAdapter.convert(evaluationRequestDto.getClassifierOptions());
        var evaluationWebRequestDataModel = new EvaluationWebRequestDataModel();
        evaluationWebRequestDataModel.setRequestId(requestId);
        evaluationWebRequestDataModel.setClassifier(classifier);
        evaluationWebRequestDataModel.setCreatedBy(userService.getCurrentUser());
        evaluationWebRequestDataModel.setEvaluationMethod(evaluationRequestDto.getEvaluationMethod());
        evaluationWebRequestDataModel.setNumFolds(evaluationRequestDto.getNumFolds());
        evaluationWebRequestDataModel.setNumTests(evaluationRequestDto.getNumTests());
        evaluationWebRequestDataModel.setSeed(evaluationRequestDto.getSeed());
        evaluationProcessManager.createEvaluationWebRequest(evaluationWebRequestDataModel);
        var evaluationLog = evaluationLogRepository.findByRequestId(requestId)
                .orElseThrow(() -> new EntityNotFoundException(EvaluationLog.class, requestId));
        log.info("Evaluation request [{}] has been created for instances uuid [{}].", requestId,
                evaluationRequestDto.getInstancesUuid());
        return CreateEvaluationResponseDto.builder()
                .id(evaluationLog.getId())
                .requestId(evaluationLog.getRequestId())
                .build();
    }
}
