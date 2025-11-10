package com.ecaservice.core.push.client.service;

import com.ecaservice.core.push.client.config.EcaWebPushClientProperties;
import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.annotation.Retryable;
import com.ecaservice.web.push.dto.AbstractPushRequest;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.core.push.client.config.rabbit.WebPushClientRabbitConfiguration.WEB_PUSH_RABBIT_TEMPLATE;

/**
 * Simple web push sender service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@Retryable
public class SimpleWebPushSender implements WebPushSender {

    private final EcaWebPushClientProperties ecaWebPushClientProperties;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Constructor with parameters.
     *
     * @param ecaWebPushClientProperties - eca web push client properties
     * @param rabbitTemplate             - rabbit template
     */
    public SimpleWebPushSender(EcaWebPushClientProperties ecaWebPushClientProperties,
                               @Qualifier(WEB_PUSH_RABBIT_TEMPLATE) RabbitTemplate rabbitTemplate) {
        this.ecaWebPushClientProperties = ecaWebPushClientProperties;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @NewSpan
    @Retry(value = "webPushRequest", requestIdKey = "#pushRequest.requestId")
    public void sendPush(AbstractPushRequest pushRequest) {
        putMdc(TX_ID, pushRequest.getCorrelationId());
        String queueName = ecaWebPushClientProperties.getRabbit().getQueueName();
        log.info("Starting to send push request [{}], type [{}], message type [{}] to queue [{}]",
                pushRequest.getRequestId(), pushRequest.getPushType(), pushRequest.getMessageType(), queueName);
        rabbitTemplate.convertAndSend(queueName, pushRequest);
        log.info("Push request [{}], type [{}], message type [{}] has been sent to queue [{}]",
                pushRequest.getRequestId(), pushRequest.getPushType(), pushRequest.getMessageType(), queueName);
    }
}
