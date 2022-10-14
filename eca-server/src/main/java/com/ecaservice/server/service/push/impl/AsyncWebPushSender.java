package com.ecaservice.server.service.push.impl;

import com.ecaservice.server.service.push.WebPushSender;
import com.ecaservice.web.push.dto.AbstractPushRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.ecaservice.server.config.EcaServiceConfiguration.ECA_THREAD_POOL_TASK_EXECUTOR;

/**
 * Async web push sender.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@Primary
public class AsyncWebPushSender implements WebPushSender {

    private final WebPushSender webPushSender;

    /**
     * Constructor with parameters.
     *
     * @param webPushSender - simple web push sender
     */
    public AsyncWebPushSender(@Qualifier("simpleWebPushSender") WebPushSender webPushSender) {
        this.webPushSender = webPushSender;
    }

    @Override
    @Async(ECA_THREAD_POOL_TASK_EXECUTOR)
    public void send(AbstractPushRequest pushRequest) {
        try {
            webPushSender.send(pushRequest);
        } catch (Exception ex) {
            log.error("Error while sent push request [{}], type [{}], message type [{}]: {}",
                    pushRequest.getRequestId(), pushRequest.getPushType(), pushRequest.getMessageType(),
                    ex.getMessage());
        }
    }
}
