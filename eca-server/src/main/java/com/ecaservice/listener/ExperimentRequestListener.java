package com.ecaservice.listener;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.exception.experiment.ExperimentException;
import com.ecaservice.mapping.EcaResponseMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.experiment.ExperimentRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

import static com.ecaservice.util.Utils.buildErrorResponse;

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
    private final ExperimentRequestService experimentRequestService;
    private final EcaResponseMapper ecaResponseMapper;

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
        try {
            Experiment experiment = experimentRequestService.createExperimentRequest(evaluationRequest);
            return ecaResponseMapper.map(experiment);
        } catch (ExperimentException ex) {
            return buildErrorResponse(ex.getMessage());
        }
    }
}
