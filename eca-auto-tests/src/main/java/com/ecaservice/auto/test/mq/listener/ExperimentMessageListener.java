package com.ecaservice.auto.test.mq.listener;

import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.repository.autotest.ExperimentRequestRepository;
import com.ecaservice.auto.test.service.EvaluationRequestService;
import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.base.model.TechnicalStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.ecaservice.auto.test.util.Utils.getFirstErrorMessage;

/**
 * Implements experiment message listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentMessageListener {

    private final EvaluationRequestService evaluationRequestService;
    private final ExperimentRequestRepository experimentRequestRepository;

    /**
     * Handles experiment response message from eca - server.
     *
     * @param experimentResponse - experiment response
     * @param message            - original message
     */
    @RabbitListener(queues = "${queue.experimentReplyToQueue}")
    public void handleMessage(ExperimentResponse experimentResponse, Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        log.info("Received MQ message with correlation id [{}]", correlationId);
        var experimentRequestEntity = experimentRequestRepository.findByCorrelationId(correlationId);
        if (experimentRequestEntity == null) {
            log.warn("Experiment request entity not found with correlation id [{}]", correlationId);
        } else if (RequestStageType.EXCEEDED.equals(experimentRequestEntity.getStageType())) {
            log.warn("Can't handle message from MQ. Got exceeded experiment request entity with correlation id [{}]",
                    correlationId);
        } else {
            internalHandleResponse(experimentRequestEntity, experimentResponse, correlationId);
        }
    }

    private void internalHandleResponse(ExperimentRequestEntity experimentRequestEntity,
                                        ExperimentResponse experimentResponse,
                                        String correlationId) {
        experimentRequestEntity.setRequestId(experimentResponse.getRequestId());
        if (TechnicalStatus.IN_PROGRESS.equals(experimentResponse.getStatus())) {
            experimentRequestEntity.setStageType(RequestStageType.REQUEST_CREATED);
            experimentRequestRepository.save(experimentRequestEntity);
            log.info("Experiment request [{}] has been created for correlation id [{}]",
                    experimentResponse.getRequestId(),
                    correlationId);
        } else if (TechnicalStatus.SUCCESS.equals(experimentResponse.getStatus())) {
            experimentRequestEntity.setStageType(RequestStageType.REQUEST_FINISHED);
            experimentRequestEntity.setDownloadUrl(experimentResponse.getDownloadUrl());
            experimentRequestRepository.save(experimentRequestEntity);
            log.info("Experiment request [{}] has been finished for correlation id [{}]",
                    experimentResponse.getRequestId(),
                    correlationId);
        } else {
            log.info("Got error response [{}] for experiment [{}], correlation id [{}]", experimentResponse.getStatus(),
                    experimentResponse.getRequestId(), correlationId);
            String errorMessage = getFirstErrorMessage(experimentResponse);
            evaluationRequestService.finishWithError(experimentRequestEntity, errorMessage);
        }
        log.info("Message [{}] response has been processed", experimentRequestEntity.getCorrelationId());
    }
}
