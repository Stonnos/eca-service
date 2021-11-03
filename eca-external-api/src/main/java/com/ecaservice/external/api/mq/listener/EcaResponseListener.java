package com.ecaservice.external.api.mq.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.service.EcaResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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

    /**
     * Handles evaluation response message from eca - server.
     *
     * @param evaluationResponse - evaluation response
     * @param message            - message
     */
    @RabbitListener(queues = "${queue.evaluationResponseQueue}")
    public void handleEvaluationMessage(EvaluationResponse evaluationResponse, Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        log.info("Received response from eca - server with correlation id [{}], status [{}]", correlationId,
                evaluationResponse.getStatus());
        var evaluationRequestEntity =
                evaluationRequestRepository.findByCorrelationId(correlationId)
                        .orElse(null);
        if (evaluationRequestEntity == null) {
            log.warn("Can't find request entity with correlation id [{}]. ", correlationId);
            return;
        }
        if (RequestStageType.EXCEEDED.equals(evaluationRequestEntity.getRequestStage())) {
            log.warn("Got exceeded eca request entity [{}]. ", correlationId);
        } else {
            evaluationRequestEntity.setRequestId(evaluationResponse.getRequestId());
            evaluationRequestEntity.setTechnicalStatus(evaluationResponse.getStatus());
            evaluationRequestEntity.setRequestStage(RequestStageType.RESPONSE_RECEIVED);
            evaluationRequestRepository.save(evaluationRequestEntity);
            ecaResponseHandler.handleResponse(evaluationRequestEntity, evaluationResponse);
        }
    }
}
