package com.ecaservice.server.mq.listener;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.event.model.ExperimentResponseEvent;
import com.ecaservice.server.event.model.push.ExperimentSystemPushEvent;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.model.entity.Experiment;
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

    private final ExperimentService experimentService;
    private final ExperimentMapper experimentMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Handles experiment request message.
     *
     * @param experimentRequest - experiment request
     */
    @RabbitListener(queues = "${queue.experimentRequestQueue}")
    public void handleMessage(@Valid @Payload ExperimentRequest experimentRequest, Message inboundMessage) {
        String requestId = UUID.randomUUID().toString();
        putMdc(TX_ID, requestId);
        putMdc(EV_REQUEST_ID, requestId);
        var experimentRequestData = experimentMapper.map(experimentRequest, inboundMessage);
        experimentRequestData.setRequestId(requestId);
        Experiment experiment = experimentService.createExperiment(experimentRequestData);
        eventPublisher.publishEvent(new ExperimentResponseEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentSystemPushEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
    }
}
