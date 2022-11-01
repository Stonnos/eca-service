package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Abstract eca response handler.
 *
 * @param <R> - request entity generic type
 * @param <M> - eca response generic type
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEcaResponseHandler<R extends EcaRequestEntity, M extends EcaResponse> {

    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Handles response from eca - server.
     *
     * @param requestEntity - request entity
     * @param ecaResponse   - response from eca - server
     */
    public void handleResponse(R requestEntity, M ecaResponse) {
        log.info("Starting to process evaluation response with correlation id [{}]",
                requestEntity.getCorrelationId());
        try {
            if (!TechnicalStatus.SUCCESS.equals(ecaResponse.getStatus())) {
                handleError(requestEntity, ecaResponse);
            } else {
                internalHandleSuccessResponse(requestEntity, ecaResponse);
                requestEntity.setRequestStage(RequestStageType.COMPLETED);
            }
            log.info("Response with correlation id [{}] has been processed", requestEntity.getCorrelationId());
        } catch (Exception ex) {
            log.error("There was an error while handle response [{}]: {}",
                    requestEntity.getCorrelationId(), ex.getMessage(), ex);
            requestEntity.setRequestStage(RequestStageType.ERROR);
            requestEntity.setErrorMessage(ex.getMessage());
        } finally {
            requestEntity.setEndDate(LocalDateTime.now());
            ecaRequestRepository.save(requestEntity);
        }
    }

    protected abstract void internalHandleSuccessResponse(R requestEntity, M ecaResponse);

    private void handleError(EcaRequestEntity ecaRequestEntity, EcaResponse ecaResponse) {
        ecaRequestEntity.setRequestStage(RequestStageType.ERROR);
        Optional.ofNullable(ecaResponse.getErrors())
                .map(messageErrors -> messageErrors.iterator().next())
                .ifPresent(error -> {
                    ecaRequestEntity.setErrorCode(error.getCode());
                    ecaRequestEntity.setErrorMessage(error.getMessage());
                });
    }
}
