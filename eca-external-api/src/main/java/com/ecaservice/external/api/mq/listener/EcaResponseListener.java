package com.ecaservice.external.api.mq.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.metrics.MetricsService;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.service.EcaResponseHandler;
import com.ecaservice.external.api.service.MessageCorrelationService;
import com.ecaservice.external.api.service.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

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
    private final ResponseBuilder responseBuilder;
    private final MessageCorrelationService messageCorrelationService;
    private final MetricsService metricsService;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Handles evaluation response message from eca - server.
     *
     * @param evaluationResponse - evaluation response
     * @param message            - message
     */
    @RabbitListener(queues = "${queue.evaluationRequestReplyToQueue}")
    public void handleEvaluationMessage(EvaluationResponse evaluationResponse, Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        log.debug("Received response from eca - server with correlation id [{}], status [{}]", correlationId,
                evaluationResponse.getStatus());
        EvaluationRequestEntity evaluationRequestEntity =
                evaluationRequestRepository.findByCorrelationId(correlationId);
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
            messageCorrelationService.pop(correlationId).ifPresent(sink -> {
                EvaluationResponseDto evaluationResponseDto =
                        responseBuilder.buildResponse(evaluationResponse, evaluationRequestEntity);
                metricsService.trackResponse(evaluationRequestEntity, evaluationResponseDto.getStatus());
                log.debug("Send response back for correlation id [{}]", correlationId);
                sink.success(evaluationResponseDto);
            });
        }
    }
}
