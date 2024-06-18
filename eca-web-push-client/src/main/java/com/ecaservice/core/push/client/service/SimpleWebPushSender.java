package com.ecaservice.core.push.client.service;

import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.annotation.Retryable;
import com.ecaservice.web.push.dto.AbstractPushRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.core.redelivery.config.RedeliveryCoreAutoConfiguration.FEIGN_EXCEPTION_STRATEGY;

/**
 * Simple web push sender service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@Retryable
@RequiredArgsConstructor
public class SimpleWebPushSender implements WebPushSender {

    private final WebPushClient webPushClient;

    @Override
    @Retry(value = "webPushRequest", exceptionStrategy = FEIGN_EXCEPTION_STRATEGY,
            requestIdKey = "#pushRequest.requestId")
    public void send(AbstractPushRequest pushRequest) {
        putMdc(TX_ID, pushRequest.getCorrelationId());
        log.info("Starting to send push request [{}], type [{}], message type [{}]", pushRequest.getRequestId(),
                pushRequest.getPushType(), pushRequest.getMessageType());
        webPushClient.sendPush(pushRequest);
        log.info("Push request [{}], type [{}], message type [{}] has been sent", pushRequest.getRequestId(),
                pushRequest.getPushType(), pushRequest.getMessageType());
    }
}
