package com.ecaservice.external.api.mq.listener;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.repository.ExperimentRequestRepository;
import com.ecaservice.external.api.service.EcaResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Eca response message listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EcaResponseListener {

    private final EcaResponseHandler ecaResponseHandler;
    private final EvaluationRequestRepository evaluationRequestRepository;
    private final ExperimentRequestRepository experimentRequestRepository;
    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Handles evaluation response message from eca - server.
     *
     * @param evaluationResponse - evaluation response
     * @param message            - message
     */
    @RabbitListener(queues = "${queue.evaluationResponseQueue}")
    public void handleEvaluationMessage(EvaluationResponse evaluationResponse, Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        log.info("Received evaluation response from eca - server with correlation id [{}], status [{}]", correlationId,
                evaluationResponse.getStatus());
        internalHandleResponse(correlationId, evaluationResponse, evaluationRequestRepository::findByCorrelationId,
                ecaResponseHandler::handleEvaluationResponse);
    }

    /**
     * Handles experiment response message from eca - server.
     *
     * @param experimentResponse - experiment response
     * @param message            - message
     */
    @RabbitListener(queues = "${queue.experimentResponseQueue}")
    public void handleExperimentMessage(ExperimentResponse experimentResponse, Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        log.info("Received experiment response from eca - server with correlation id [{}], status [{}]", correlationId,
                experimentResponse.getStatus());
        internalHandleResponse(correlationId, experimentResponse, experimentRequestRepository::findByCorrelationId,
                ecaResponseHandler::handleExperimentResponse);
    }

    private <R extends EcaRequestEntity, M extends EcaResponse> void internalHandleResponse(String correlationId,
                                                                                            M ecaResponse,
                                                                                            Function<String, Optional<R>> getRequestFunction,
                                                                                            BiConsumer<R, M> responseHandler) {
        R requestEntity = getRequestFunction.apply(correlationId).orElse(null);
        if (requestEntity == null) {
            log.warn("Can't find request entity with correlation id [{}]. ", correlationId);
            return;
        }
        if (RequestStageType.EXCEEDED.equals(requestEntity.getRequestStage())) {
            log.warn("Got exceeded eca request entity [{}]. ", correlationId);
        } else {
            requestEntity.setRequestId(ecaResponse.getRequestId());
            requestEntity.setTechnicalStatus(ecaResponse.getStatus());
            requestEntity.setRequestStage(RequestStageType.RESPONSE_RECEIVED);
            ecaRequestRepository.save(requestEntity);
            responseHandler.accept(requestEntity, ecaResponse);
        }
    }
}
