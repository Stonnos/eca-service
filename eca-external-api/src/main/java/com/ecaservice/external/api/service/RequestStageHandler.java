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
     * @param ex               - exception
     */
    public void handleError(EcaRequestEntity ecaRequestEntity, Exception ex) {
        internalHandle(ecaRequestEntity, RequestStageType.ERROR, ex.getMessage());
    }

    /**
     * Handle exceeded request.
     *
     * @param ecaRequestEntity - eca request entity
     */
    public void handleExceeded(EcaRequestEntity ecaRequestEntity) {
        internalHandle(ecaRequestEntity, RequestStageType.EXCEEDED,
                String.format("Timeout after %d minutes", externalApiConfig.getRequestTimeoutMinutes()));
    }

    private void internalHandle(EcaRequestEntity ecaRequestEntity, RequestStageType requestStageType,
                                String errorMessage) {
        ecaRequestEntity.setRequestStage(requestStageType);
        ecaRequestEntity.setErrorMessage(errorMessage);
        ecaRequestEntity.setEndDate(LocalDateTime.now());
        ecaRequestRepository.save(ecaRequestEntity);
    }
}
