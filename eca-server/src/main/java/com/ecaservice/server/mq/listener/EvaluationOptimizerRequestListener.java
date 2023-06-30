package com.ecaservice.server.mq.listener;

import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.server.event.model.EvaluationErsReportEvent;
import com.ecaservice.server.event.model.EvaluationResponseEvent;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import com.ecaservice.server.service.evaluation.EvaluationOptimizerService;
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

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.server.util.InstancesUtils.md5Hash;

/**
 * Rabbit MQ listener for evaluation optimizer request messages.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "rabbit.enabled", havingValue = "true")
@RequiredArgsConstructor
public class EvaluationOptimizerRequestListener {

    private final EvaluationOptimizerService evaluationOptimizerService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Handles evaluation optimizer request message.
     *
     * @param instancesRequest - instances request
     */
    @RabbitListener(queues = "${queue.evaluationOptimizerRequestQueue}")
    public void handleMessage(@Valid @Payload InstancesRequest instancesRequest, Message inboundMessage) {
        MessageProperties inboundMessageProperties = inboundMessage.getMessageProperties();
        log.info("Received evaluation optimizer request with correlation id [{}]",
                inboundMessageProperties.getCorrelationId());
        String requestId = UUID.randomUUID().toString();
        putMdc(TX_ID, requestId);
        String dataMd5Hash = md5Hash(instancesRequest.getData());
        var instancesRequestDataModel =
                new InstancesRequestDataModel(requestId, dataMd5Hash, instancesRequest.getData());
        EvaluationResultsDataModel evaluationResultsDataModel =
                evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequestDataModel);
        log.info("Evaluation response [{}] with status [{}] has been built for evaluation optimizer request.",
                evaluationResultsDataModel.getRequestId(), evaluationResultsDataModel.getStatus());
        eventPublisher.publishEvent(new EvaluationErsReportEvent(this, evaluationResultsDataModel));
        eventPublisher.publishEvent(new EvaluationResponseEvent(this, evaluationResultsDataModel,
                inboundMessageProperties.getCorrelationId(), inboundMessageProperties.getReplyTo()));
        log.info("Evaluation optimizer request with correlation id [{}] has been processed",
                inboundMessageProperties.getCorrelationId());
    }
}
