package com.ecaservice.external.api.service;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
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
     * @param ecaRequestEntity - eca request entity
     * @param errorMessage     - error message
     */
    public void handleError(EcaRequestEntity ecaRequestEntity, String errorMessage) {
        internalHandle(ecaRequestEntity, RequestStageType.ERROR, errorMessage);
    }

    /**
     * Handle exceeded request.
     *
     * @param ecaRequestEntity - eca request entity
     */
    public void handleExceeded(EcaRequestEntity ecaRequestEntity) {
        internalHandle(ecaRequestEntity, RequestStageType.EXCEEDED,
                String.format("Timeout after %d seconds", externalApiConfig.getRequestTimeoutSeconds()));
    }

    private void internalHandle(EcaRequestEntity ecaRequestEntity, RequestStageType requestStageType,
                                String errorMessage) {
        ecaRequestEntity.setRequestStage(requestStageType);
        ecaRequestEntity.setErrorMessage(errorMessage);
        ecaRequestEntity.setEndDate(LocalDateTime.now());
        ecaRequestRepository.save(ecaRequestEntity);
    }
}
