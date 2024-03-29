package com.ecaservice.external.api.service;

import com.ecaservice.external.api.dto.EvaluationErrorCode;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Eca request stage service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestStageHandler {

    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Handle error request.
     *
     * @param ecaRequestEntity - eca request entity
     * @param errorMessage     - error message
     */
    public void handleError(EcaRequestEntity ecaRequestEntity, String errorMessage) {
        internalHandle(ecaRequestEntity, RequestStageType.ERROR, EvaluationErrorCode.INTERNAL_SERVER_ERROR,
                errorMessage);
        log.info("Eca request [{}] has been completed with error", ecaRequestEntity.getCorrelationId());
    }

    /**
     * Handle exceeded request.
     *
     * @param ecaRequestEntity - eca request entity
     */
    public void handleExceeded(EcaRequestEntity ecaRequestEntity) {
        internalHandle(ecaRequestEntity, RequestStageType.EXCEEDED, null, null);
        log.info("Exceeded request with correlation id [{}]", ecaRequestEntity.getCorrelationId());
    }

    private void internalHandle(EcaRequestEntity ecaRequestEntity,
                                RequestStageType requestStageType,
                                EvaluationErrorCode evaluationErrorCode,
                                String errorMessage) {
        ecaRequestEntity.setRequestStage(requestStageType);
        ecaRequestEntity.setErrorCode(evaluationErrorCode);
        ecaRequestEntity.setErrorMessage(errorMessage);
        ecaRequestEntity.setEndDate(LocalDateTime.now());
        ecaRequestRepository.save(ecaRequestEntity);
    }
}
