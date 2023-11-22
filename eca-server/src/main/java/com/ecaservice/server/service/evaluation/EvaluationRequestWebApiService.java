package com.ecaservice.server.service.evaluation;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.server.bpm.model.EvaluationRequestModel;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.dto.CreateEvaluationRequestDto;
import com.ecaservice.server.dto.CreateOptimalEvaluationRequestDto;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.UserService;
import com.ecaservice.web.dto.model.CreateEvaluationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Supplier;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.server.config.audit.AuditCodes.CREATE_EVALUATION_REQUEST;
import static com.ecaservice.server.config.audit.AuditCodes.CREATE_OPTIMAL_EVALUATION_REQUEST;

/**
 * Evaluation request web api service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationRequestWebApiService {

    private final CrossValidationConfig crossValidationConfig;
    private final UserService userService;
    private final EvaluationProcessManager evaluationProcessManager;
    private final EvaluationLogMapper evaluationLogMapper;
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
        log.info(
                "Starting to create evaluation [{}] request for instances [{}], classifier options [{}], evaluation method [{}]",
                requestId,
                evaluationRequestDto.getInstancesUuid(),
                evaluationRequestDto.getClassifierOptions().getClass().getSimpleName(),
                evaluationRequestDto.getEvaluationMethod());
        var createEvaluationResponseDto = internalCreateEvaluationRequest(requestId,
                () -> evaluationLogMapper.map(evaluationRequestDto, crossValidationConfig));
        log.info("Evaluation request [{}] has been created for instances uuid [{}].",
                createEvaluationResponseDto.getRequestId(), evaluationRequestDto.getInstancesUuid());
        return createEvaluationResponseDto;
    }

    /**
     * Creates optimal evaluation request.
     *
     * @param evaluationRequestDto - optimal evaluation request dto
     * @return evaluation response
     */
    @Audit(value = CREATE_OPTIMAL_EVALUATION_REQUEST, correlationIdKey = "#result.requestId")
    public CreateEvaluationResponseDto createOptimalEvaluationRequest(
            CreateOptimalEvaluationRequestDto evaluationRequestDto) {
        String requestId = UUID.randomUUID().toString();
        putMdc(TX_ID, requestId);
        log.info(
                "Starting to create optimal evaluation [{}] request for instances [{}], evaluation method [{}]",
                requestId, evaluationRequestDto.getInstancesUuid(), evaluationRequestDto.getEvaluationMethod());
        var createEvaluationResponseDto = internalCreateEvaluationRequest(requestId,
                () -> evaluationLogMapper.map(evaluationRequestDto, crossValidationConfig));
        log.info("Optimal evaluation request [{}] has been created for instances uuid [{}].",
                createEvaluationResponseDto.getRequestId(), evaluationRequestDto.getInstancesUuid());
        return createEvaluationResponseDto;
    }

    private CreateEvaluationResponseDto internalCreateEvaluationRequest(
            String requestId,
            Supplier<EvaluationRequestModel> evaluationRequestModelSupplier) {
        var evaluationRequestModel = evaluationRequestModelSupplier.get();
        evaluationRequestModel.setRequestId(requestId);
        evaluationRequestModel.setCreatedBy(userService.getCurrentUser());
        evaluationProcessManager.createAndProcessEvaluationRequest(evaluationRequestModel);
        var evaluationLog = evaluationLogRepository.findByRequestId(requestId)
                .orElseThrow(() -> new EntityNotFoundException(EvaluationLog.class, requestId));
        return CreateEvaluationResponseDto.builder()
                .id(evaluationLog.getId())
                .requestId(evaluationLog.getRequestId())
                .build();
    }
}
