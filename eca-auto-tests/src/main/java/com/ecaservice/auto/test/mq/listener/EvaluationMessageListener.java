package com.ecaservice.auto.test.mq.listener;

import com.ecaservice.auto.test.entity.autotest.EvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.repository.autotest.EvaluationRequestRepository;
import com.ecaservice.auto.test.service.EvaluationRequestService;
import com.ecaservice.auto.test.service.EvaluationResultsProcessor;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.ecaservice.auto.test.util.Utils.getFirstErrorMessage;

/**
 * Implements rabbit message listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationMessageListener {

    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationResultsProcessor evaluationResultsProcessor;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Handles evaluation response message from eca - server.
     *
     * @param evaluationResponse - evaluation response
     * @param message            - original message
     */
    @RabbitListener(queues = "${queue.evaluationReplyToQueue}")
    public void handleMessage(EvaluationResponse evaluationResponse, Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        log.info("Received evaluation response MQ message with correlation id [{}]", correlationId);
        var evaluationRequestEntity = evaluationRequestRepository.findByCorrelationId(correlationId)
                .orElseThrow(() -> new EntityNotFoundException(EvaluationRequestEntity.class, correlationId));
        if (RequestStageType.EXCEEDED.equals(evaluationRequestEntity.getStageType())) {
            log.warn("Can't handle message from MQ. Got exceeded evaluation request entity with correlation id [{}]",
                    correlationId);
        } else {
            internalHandleResponse(evaluationRequestEntity, evaluationResponse, correlationId);
        }
    }

    private void internalHandleResponse(EvaluationRequestEntity evaluationRequestEntity,
                                        EvaluationResponse evaluationResponse,
                                        String correlationId) {
        evaluationRequestEntity.setRequestId(evaluationResponse.getRequestId());
        if (TechnicalStatus.SUCCESS.equals(evaluationResponse.getStatus())) {
            evaluationResultsProcessor.compareAndMatchEvaluationResults(evaluationRequestEntity,
                    evaluationResponse.getEvaluationResults());
        } else {
            log.info("Got error response [{}] for evaluation request [{}], correlation id [{}]",
                    evaluationResponse.getStatus(),
                    evaluationResponse.getRequestId(), correlationId);
            String errorMessage = getFirstErrorMessage(evaluationResponse);
            evaluationRequestService.finishWithError(evaluationRequestEntity, errorMessage);
        }
        log.info("Message [{}] response has been processed", evaluationRequestEntity.getCorrelationId());
    }
}
