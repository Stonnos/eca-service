package com.ecaservice.core.push.client.service;

import com.ecaservice.web.push.dto.AbstractPushRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import static com.ecaservice.core.push.client.config.EcaWebPushClientAutoConfiguration.WEB_PUSH_CLIENT_THREAD_POOL_TASK_EXECUTOR;

/**
 * Async web push sender.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "web-push.client.async", havingValue = "true")
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

    /**
     * Init method.
     */
    @PostConstruct
    public void init() {
        log.info("Web push client async sender has been configured");
    }

    @Override
    @Async(WEB_PUSH_CLIENT_THREAD_POOL_TASK_EXECUTOR)
    public void sendPush(AbstractPushRequest pushRequest) {
        webPushSender.sendPush(pushRequest);
    }
}
