package com.ecaservice.external.api.service;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.exception.EntityNotFoundException;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Eca request stage service.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class RequestStageHandler {

    private final ExternalApiConfig externalApiConfig;
    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Handle error request.
     *
     * @param correlationId - correlation id
     * @param ex            - exception
     */
    public void handleError(String correlationId, Exception ex) {
        internalHandle(correlationId, RequestStageType.ERROR, ex.getMessage());
    }

    /**
     * Handle exceeded request.
     *
     * @param correlationId - correlation id
     */
    public void handleExceeded(String correlationId) {
        internalHandle(correlationId, RequestStageType.EXCEEDED,
                String.format("Timeout after %d minutes", externalApiConfig.getRequestTimeoutMinutes()));
    }

    private void internalHandle(String correlationId, RequestStageType requestStageType, String errorMessage) {
        EcaRequestEntity ecaRequestEntity = getByCorrelationId(correlationId);
        ecaRequestEntity.setRequestStage(requestStageType);
        ecaRequestEntity.setErrorMessage(errorMessage);
        ecaRequestEntity.setEndDate(LocalDateTime.now());
        ecaRequestRepository.save(ecaRequestEntity);
    }

    private EcaRequestEntity getByCorrelationId(String correlationId) {
        return ecaRequestRepository.findByCorrelationId(correlationId).orElseThrow(
                () -> new EntityNotFoundException(EcaRequestEntity.class, correlationId));
    }
}
