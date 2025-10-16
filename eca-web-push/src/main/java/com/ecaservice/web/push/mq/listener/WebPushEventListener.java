package com.ecaservice.web.push.mq.listener;

import com.ecaservice.web.push.dto.AbstractPushRequest;
import com.ecaservice.web.push.service.handler.AbstractPushRequestHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Web push message listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Component
@ConditionalOnProperty(value = "app.rabbit.enabled", havingValue = "true")
@RequiredArgsConstructor
public class WebPushEventListener {

    private final List<AbstractPushRequestHandler> pushRequestHandlers;

    /**
     * Handles push event.
     *
     * @param pushRequest - push request
     */
    @RabbitListener(queues = "${app.rabbit.queueName}")
    @SuppressWarnings("unchecked")
    public void handlePushEvent(@Valid AbstractPushRequest pushRequest) {
        log.info("Received push request [{}], correlation id [{}] with type [{}], message code [{}]",
                pushRequest.getRequestId(), pushRequest.getCorrelationId(), pushRequest.getPushType(),
                pushRequest.getMessageType());
        var handler = getRequestHandler(pushRequest);
        handler.handle(pushRequest);
        log.info("Push request [{}] with type [{}], correlation id [{}], message code [{}] has been processed",
                pushRequest.getRequestId(),
                pushRequest.getPushType(), pushRequest.getCorrelationId(), pushRequest.getMessageType());
    }

    private AbstractPushRequestHandler getRequestHandler(AbstractPushRequest pushRequest) {
        return pushRequestHandlers.stream()
                .filter(handler -> handler.canHandle(pushRequest))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't handle push type [%s]", pushRequest.getPushType())));
    }
}
