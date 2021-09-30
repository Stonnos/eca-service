package com.ecaservice.server.mq.listener;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.event.model.ExperimentResponseEvent;
import com.ecaservice.server.event.model.ExperimentWebPushEvent;
import com.ecaservice.server.model.MsgProperties;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.service.experiment.ExperimentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

    private final ExperimentService experimentService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Handles experiment request message.
     *
     * @param experimentRequest - experiment request
     */
    @RabbitListener(queues = "${queue.experimentRequestQueue}")
    public void handleMessage(@Valid @Payload ExperimentRequest experimentRequest, Message inboundMessage) {
        MsgProperties msgProperties = MsgProperties.builder()
                .channel(Channel.QUEUE)
                .replyTo(inboundMessage.getMessageProperties().getReplyTo())
                .correlationId(inboundMessage.getMessageProperties().getCorrelationId())
                .build();
        Experiment experiment = experimentService.createExperiment(experimentRequest, msgProperties);
        log.info("Experiment request [{}] has been created.", experiment.getRequestId());
        eventPublisher.publishEvent(new ExperimentResponseEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentWebPushEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
    }
}
