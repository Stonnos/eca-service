package com.ecaservice.server.mq.listener;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.server.event.model.EvaluationErsReportEvent;
import com.ecaservice.server.event.model.EvaluationResponseEvent;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.evaluation.EvaluationMessageRequestDataModel;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.server.service.evaluation.EvaluationRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.UUID;

/**
 * Rabbit MQ listener for evaluation request messages.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "rabbit.enabled", havingValue = "true")
@RequiredArgsConstructor
public class EvaluationRequestListener {

    private final EvaluationRequestService evaluationRequestService;
    private final ApplicationEventPublisher eventPublisher;
    private final EvaluationLogMapper evaluationLogMapper;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;

    /**
     * Handles evaluation request message.
     *
     * @param evaluationRequest - evaluation request
     */
    @RabbitListener(queues = "${queue.evaluationRequestQueue}")
    public void handleMessage(@Valid @Payload EvaluationRequest evaluationRequest, Message inboundMessage) {
        MessageProperties inboundMessageProperties = inboundMessage.getMessageProperties();
        log.info("Received evaluation request with correlation id [{}]", inboundMessageProperties.getCorrelationId());
        var evaluationRequestDataModel = prepareEvaluationRequestDataModel(evaluationRequest);
        EvaluationResultsDataModel evaluationResultsDataModel =
                evaluationRequestService.createAndProcessEvaluationRequest(evaluationRequestDataModel);
        log.info("Evaluation response [{}] with status [{}] has been built.",
                evaluationResultsDataModel.getRequestId(), evaluationResultsDataModel.getStatus());
        eventPublisher.publishEvent(new EvaluationErsReportEvent(this, evaluationResultsDataModel));
        eventPublisher.publishEvent(new EvaluationResponseEvent(this, evaluationResultsDataModel,
                inboundMessageProperties.getCorrelationId(), inboundMessageProperties.getReplyTo()));
        log.info("Evaluation request with correlation id [{}] has been processed",
                inboundMessageProperties.getCorrelationId());
    }

    private EvaluationMessageRequestDataModel prepareEvaluationRequestDataModel(EvaluationRequest evaluationRequest) {
        var evaluationRequestDataModel = evaluationLogMapper.map(evaluationRequest);
        evaluationRequestDataModel.setRequestId(UUID.randomUUID().toString());
        evaluationRequestDataModel.setClassifier(
                classifierOptionsAdapter.convert(evaluationRequest.getClassifierOptions()));
        return evaluationRequestDataModel;
    }
}
