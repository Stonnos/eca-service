package com.ecaservice.mq.listener;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.event.model.ExperimentEmailEvent;
import com.ecaservice.event.model.ExperimentWebPushEvent;
import com.ecaservice.mapping.EcaResponseMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.experiment.ExperimentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

/**
 * Rabbit MQ listener for experiment request messages.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentRequestListener {

    private final RabbitTemplate rabbitTemplate;
    private final ExperimentService experimentService;
    private final EcaResponseMapper ecaResponseMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Handles experiment request message.
     *
     * @param experimentRequest - experiment request
     */
    @RabbitListener(queues = "${queue.experimentRequestQueue}")
    public void handleMessage(@Valid @Payload ExperimentRequest experimentRequest, Message inboundMessage) {
        EcaResponse ecaResponse = createExperimentRequest(experimentRequest);
        MessageProperties inboundMessageProperties = inboundMessage.getMessageProperties();
        rabbitTemplate.convertAndSend(inboundMessageProperties.getReplyTo(), ecaResponse, outboundMessage -> {
            outboundMessage.getMessageProperties().setCorrelationId(inboundMessageProperties.getCorrelationId());
            return outboundMessage;
        });
    }

    private EcaResponse createExperimentRequest(ExperimentRequest evaluationRequest) {
        Experiment experiment = experimentService.createExperiment(evaluationRequest);
        eventPublisher.publishEvent(new ExperimentWebPushEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
        log.info("Experiment request [{}] has been created.", experiment.getRequestId());
        return ecaResponseMapper.map(experiment);
    }
}
