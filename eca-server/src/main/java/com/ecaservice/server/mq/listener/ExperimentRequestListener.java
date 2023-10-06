package com.ecaservice.server.mq.listener;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.service.experiment.ExperimentProcessManager;
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
 * Rabbit MQ listener for experiment request messages.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "rabbit.enabled", havingValue = "true")
@RequiredArgsConstructor
public class ExperimentRequestListener {

    private final ExperimentMapper experimentMapper;
    private final ExperimentProcessManager experimentProcessManager;

    /**
     * Handles experiment request message.
     *
     * @param experimentRequest - experiment request
     */
    @RabbitListener(queues = "${queue.experimentRequestQueue}")
    public void handleMessage(@Valid @Payload ExperimentRequest experimentRequest, Message inboundMessage) {
        MessageProperties inboundMessageProperties = inboundMessage.getMessageProperties();
        log.info("Received experiment [{}] request with correlation id [{}]",
                experimentRequest.getExperimentType(), inboundMessageProperties.getCorrelationId());
        String requestId = UUID.randomUUID().toString();
        putMdc(TX_ID, requestId);
        putMdc(EV_REQUEST_ID, requestId);
        var experimentRequestModel = experimentMapper.map(experimentRequest, inboundMessage);
        experimentRequestModel.setRequestId(requestId);
        experimentProcessManager.createExperimentRequest(experimentRequestModel);
        log.info("Experiment [{}] request with correlation id [{}] has been processed",
                experimentRequest.getExperimentType(), inboundMessageProperties.getCorrelationId());
    }
}
