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
        log.info("Starting to handle eca response with correlation id [{}], status [{}]",
                requestEntity.getCorrelationId(), ecaResponse.getStatus());
        try {
            if (TechnicalStatus.IN_PROGRESS.equals(ecaResponse.getStatus())) {
                log.info("Eca request [{}] has been successfully created for correlation id [{}]",
                        ecaResponse.getRequestId(), requestEntity.getCorrelationId());
            } else if (TechnicalStatus.SUCCESS.equals(ecaResponse.getStatus())) {
                internalHandleSuccessResponse(requestEntity, ecaResponse);
                requestEntity.setRequestStage(RequestStageType.COMPLETED);
                requestEntity.setEndDate(LocalDateTime.now());
                ecaRequestRepository.save(requestEntity);
                log.info("Eca request [{}] has been successfully completed", requestEntity.getCorrelationId());
            } else {
                handleEcaResponseError(requestEntity, ecaResponse);
            }
            log.info("Response with correlation id [{}] has been handled", requestEntity.getCorrelationId());
        } catch (Exception ex) {
            log.error("There was an error while handle response [{}]: {}",
                    requestEntity.getCorrelationId(), ex.getMessage(), ex);
            handleError(requestEntity, ex.getMessage());
        }
    }

    protected abstract void internalHandleSuccessResponse(R requestEntity, M ecaResponse);

    private void handleError(EcaRequestEntity requestEntity, String errorMessage) {
        requestEntity.setRequestStage(RequestStageType.ERROR);
        requestEntity.setErrorMessage(errorMessage);
        requestEntity.setEndDate(LocalDateTime.now());
        ecaRequestRepository.save(requestEntity);
        log.info("Eca request [{}] has been completed with error", requestEntity.getCorrelationId());
    }

    private void handleEcaResponseError(EcaRequestEntity requestEntity, EcaResponse ecaResponse) {
        requestEntity.setRequestStage(RequestStageType.ERROR);
        Optional.ofNullable(ecaResponse.getErrors())
                .map(messageErrors -> messageErrors.iterator().next())
                .ifPresent(error -> {
                    requestEntity.setErrorCode(error.getCode());
                    requestEntity.setErrorMessage(error.getMessage());
                    log.info("Got error code [{}], message [{}] from eca response with correlation id [{}]",
                            error.getCode(), error.getMessage(), requestEntity.getCorrelationId());
                });
        requestEntity.setEndDate(LocalDateTime.now());
        ecaRequestRepository.save(requestEntity);
        log.info("Eca request [{}] has been completed with error", requestEntity.getCorrelationId());
    }
}
