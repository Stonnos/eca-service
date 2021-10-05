package com.ecaservice.server.event.listener;

import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.event.model.ExperimentResponseEvent;
import com.ecaservice.server.mapping.EcaResponseMapper;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.util.Utils.buildExperimentDownloadUrl;

/**
 * Experiment response event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentResponseEventListener {

    private final ExperimentConfig experimentConfig;
    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;
    private final EcaResponseMapper ecaResponseMapper;

    /**
     * Handles event to sent experiment response to MQ.
     *
     * @param experimentResponseEvent - experiment response event
     */
    @EventListener
    public void handleExperimentResponseEvent(ExperimentResponseEvent experimentResponseEvent) {
        Experiment experiment = experimentResponseEvent.getExperiment();
        log.info("Starting to sent experiment [{}] response for request status [{}] to MQ", experiment.getRequestId(),
                experiment.getRequestStatus());
        QueueInformation queueInformation = amqpAdmin.getQueueInfo(experiment.getReplyTo());
        if (queueInformation == null) {
            log.warn(
                    "Can't sent experiment [{}] response for request status [{}], because reply to queue doesn't exists",
                    experiment.getRequestId(), experiment.getRequestStatus());
        } else {
            ExperimentResponse experimentResponse = processExperimentResponse(experiment);
            rabbitTemplate.convertAndSend(experiment.getReplyTo(), experimentResponse, outboundMessage -> {
                outboundMessage.getMessageProperties().setCorrelationId(experiment.getCorrelationId());
                return outboundMessage;
            });
            log.info("Experiment [{}] response for request status [{}] has been sent to MQ",
                    experiment.getRequestId(), experiment.getRequestStatus());
        }
    }

    private ExperimentResponse processExperimentResponse(Experiment experiment) {
        ExperimentResponse experimentResponse = ecaResponseMapper.map(experiment);
        if (RequestStatus.FINISHED.equals(experiment.getRequestStatus())) {
            String downloadUrl =
                    buildExperimentDownloadUrl(experimentConfig.getDownloadBaseUrl(), experiment.getToken());
            experimentResponse.setDownloadUrl(downloadUrl);
        }
        return experimentResponse;
    }
}