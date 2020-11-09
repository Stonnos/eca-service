package com.ecaservice.external.api.mq.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.exception.EntityNotFoundException;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.service.EcaResponseHandler;
import com.ecaservice.external.api.service.MessageCorrelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.ecaservice.external.api.util.ResponseHelper.buildResponse;

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
    private final MessageCorrelationService messageCorrelationService;
    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Handles evaluation response message from eca - server.
     *
     * @param evaluationResponse - evaluation response
     * @param message            - message
     */
    @RabbitListener(queues = "${queue.evaluationRequestReplyToQueue}")
    public void handleEvaluationMessage(EvaluationResponse evaluationResponse, Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        log.info("Received message with correlation id [{}]", correlationId);
        EcaRequestEntity ecaRequestEntity = ecaRequestRepository.findByCorrelationId(correlationId).orElseThrow(
                () -> new EntityNotFoundException(EcaRequestEntity.class, correlationId));
        if (!RequestStageType.REQUEST_SENT.equals(ecaRequestEntity.getRequestStage())) {
            log.warn("Got eca request entity [{}] with invalid status [{}]. ", correlationId,
                    ecaRequestEntity.getRequestStage());
        } else {
            ecaRequestEntity.setRequestId(evaluationResponse.getRequestId());
            ecaRequestEntity.setStatus(evaluationResponse.getStatus());
            ecaRequestEntity.setRequestStage(RequestStageType.RESPONSE_RECEIVED);
            ecaRequestRepository.save(ecaRequestEntity);
            ecaResponseHandler.handleResponse(ecaRequestEntity, evaluationResponse);
            messageCorrelationService.pop(correlationId).ifPresent(sink -> {
                EvaluationResponseDto evaluationResponseDto = buildResponse(evaluationResponse);
                sink.success(evaluationResponseDto);
            });
        }
    }
}
