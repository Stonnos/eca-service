package com.ecaservice.external.api.mq.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.exception.EntityNotFoundException;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.service.EcaResponseHandler;
import com.ecaservice.external.api.service.MessageCorrelationService;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

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
            ecaRequestEntity.setRequestStage(RequestStageType.RESPONSE_RECEIVED);
            ecaRequestRepository.save(ecaRequestEntity);
            messageCorrelationService.pop(correlationId).ifPresent(sink -> {
                ecaResponseHandler.handleResponse(ecaRequestEntity, evaluationResponse);
                EvaluationResponseDto evaluationResponseDto = buildResponse(evaluationResponse);
                sink.success(evaluationResponseDto);
            });
        }
    }

    private EvaluationResponseDto buildResponse(EvaluationResponse evaluationResponse) {
        EvaluationResponseDto.EvaluationResponseDtoBuilder builder = EvaluationResponseDto.builder()
                .requestId(evaluationResponse.getRequestId());
        if (TechnicalStatus.SUCCESS.equals(evaluationResponse.getStatus()) &&
                Optional.ofNullable(evaluationResponse.getEvaluationResults()).map(
                        EvaluationResults::getEvaluation).isPresent()) {
            Evaluation evaluation = evaluationResponse.getEvaluationResults().getEvaluation();
            builder.numTestInstances(BigInteger.valueOf((long) evaluation.numInstances()))
                    .numCorrect(BigInteger.valueOf((long) evaluation.correct()))
                    .numIncorrect(BigInteger.valueOf((long) evaluation.incorrect()))
                    .pctCorrect(BigDecimal.valueOf(evaluation.pctCorrect()))
                    .pctIncorrect(BigDecimal.valueOf(evaluation.pctIncorrect()))
                    .meanAbsoluteError(BigDecimal.valueOf(evaluation.meanAbsoluteError()))
                    .status(RequestStatus.SUCCESS);
        } else {
            builder.status(RequestStatus.ERROR);
        }
        return builder.build();
    }
}
