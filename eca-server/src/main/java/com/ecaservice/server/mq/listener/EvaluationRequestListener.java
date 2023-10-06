package com.ecaservice.server.mq.listener;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.server.bpm.model.EvaluationRequestModel;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.service.evaluation.EvaluationProcessManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.UUID;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;

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

    private final EvaluationProcessManager evaluationProcessManager;
    private final EvaluationLogMapper evaluationLogMapper;

    /**
     * Handles evaluation request message.
     *
     * @param evaluationRequest - evaluation request
     */
    @RabbitListener(queues = "${queue.evaluationRequestQueue}")
    public void handleMessage(@Valid @Payload EvaluationRequest evaluationRequest, Message inboundMessage) {
        MessageProperties inboundMessageProperties = inboundMessage.getMessageProperties();
        log.info("Received evaluation request with correlation id [{}]", inboundMessageProperties.getCorrelationId());
        String requestId = UUID.randomUUID().toString();
        putMdc(TX_ID, requestId);
        putMdc(EV_REQUEST_ID, requestId);
        var evaluationRequestModel =
                prepareEvaluationRequestModel(evaluationRequest, inboundMessage);
        evaluationRequestModel.setRequestId(requestId);
        evaluationProcessManager.createAndProcessEvaluationRequest(evaluationRequestModel);
        log.info("Evaluation request with correlation id [{}] has been processed",
                inboundMessageProperties.getCorrelationId());
    }

    private EvaluationRequestModel prepareEvaluationRequestModel(EvaluationRequest evaluationRequest,
                                                                 Message inboundMessage) {
        var evaluationRequestDataModel = evaluationLogMapper.map(evaluationRequest);
        evaluationRequestDataModel.setCorrelationId(inboundMessage.getMessageProperties().getCorrelationId());
        evaluationRequestDataModel.setReplyTo(inboundMessage.getMessageProperties().getReplyTo());
        return evaluationRequestDataModel;
    }
}
