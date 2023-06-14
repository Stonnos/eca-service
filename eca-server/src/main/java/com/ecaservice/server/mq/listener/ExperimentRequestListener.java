package com.ecaservice.server.mq.listener;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.event.model.ExperimentResponseEvent;
import com.ecaservice.server.event.model.push.ExperimentWebPushEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.experiment.ExperimentMessageRequestData;
import com.ecaservice.server.service.experiment.ExperimentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(value = "rabbit.enabled", havingValue = "true")
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
        var experimentRequestData = createExperimentRequestData(experimentRequest, inboundMessage);
        Experiment experiment = experimentService.createExperiment(experimentRequestData);
        log.info("Experiment request [{}] has been created.", experiment.getRequestId());
        eventPublisher.publishEvent(new ExperimentResponseEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentWebPushEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
    }

    private ExperimentMessageRequestData createExperimentRequestData(ExperimentRequest experimentRequest,
                                                                     Message inboundMessage) {
        var experimentRequestData = new ExperimentMessageRequestData();
        experimentRequestData.setData(experimentRequest.getData());
        experimentRequestData.setExperimentType(experimentRequest.getExperimentType());
        experimentRequestData.setEvaluationMethod(experimentRequest.getEvaluationMethod());
        experimentRequestData.setEmail(experimentRequest.getEmail());
        experimentRequestData.setReplyTo(inboundMessage.getMessageProperties().getReplyTo());
        experimentRequestData.setCorrelationId(inboundMessage.getMessageProperties().getCorrelationId());
        return experimentRequestData;
    }
}
