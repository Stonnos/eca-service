package com.ecaservice.external.api.mq.listener;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import java.util.Optional;

/**
 * Abstract response listener.
 *
 * @param <R> - request entity generic type
 * @param <M> - eca response generic type
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractResponseListener<R extends EcaRequestEntity, M extends EcaResponse> {

    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Handles response message.
     *
     * @param ecaResponse - eca response
     * @param message     - mq original message
     */
    public void handleResponseMessage(M ecaResponse, Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        log.info("Received response from eca - server with correlation id [{}], status [{}]", correlationId,
                ecaResponse.getStatus());
        internalHandleResponse(correlationId, ecaResponse);
    }

    /**
     * Gets request entity with correlation id.
     *
     * @param correlationId - correlation id
     * @return request entity
     */
    protected abstract Optional<R> getRequestEntity(String correlationId);

    /**
     * Handles response.
     *
     * @param requestEntity - request entity
     * @param ecaResponse   - eca response
     */
    protected abstract void handleResponse(R requestEntity, M ecaResponse);

    private void internalHandleResponse(String correlationId, M ecaResponse) {
        R requestEntity = getRequestEntity(correlationId).orElse(null);
        if (requestEntity == null) {
            log.warn("Can't find request entity with correlation id [{}]. ", correlationId);
            return;
        }
        if (RequestStageType.EXCEEDED.equals(requestEntity.getRequestStage())) {
            log.warn("Got exceeded eca request entity [{}].", correlationId);
        } else {
            requestEntity.setRequestId(ecaResponse.getRequestId());
            requestEntity.setTechnicalStatus(ecaResponse.getStatus());
            requestEntity.setRequestStage(RequestStageType.RESPONSE_RECEIVED);
            ecaRequestRepository.save(requestEntity);
            handleResponse(requestEntity, ecaResponse);
        }
    }

}
