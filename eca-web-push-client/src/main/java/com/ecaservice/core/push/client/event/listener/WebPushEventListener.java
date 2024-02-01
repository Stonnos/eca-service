package com.ecaservice.core.push.client.event.listener;

import com.ecaservice.core.push.client.event.listener.handler.AbstractPushEventHandler;
import com.ecaservice.core.push.client.event.model.AbstractPushEvent;
import com.ecaservice.core.push.client.service.WebPushSender;
import com.ecaservice.core.push.client.validator.PushRequestValidator;
import com.ecaservice.web.push.dto.AbstractPushRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Web push event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebPushEventListener {

    private final List<AbstractPushEventHandler> pushEventHandlers;
    private final List<PushRequestValidator> pushRequestValidators;
    private final WebPushSender webPushSender;

    /**
     * Handles push event.
     *
     * @param pushEvent - push event
     */
    @EventListener
    @SuppressWarnings("unchecked")
    public void handlePushEvent(AbstractPushEvent pushEvent) {
        log.info("Starting to handle push event [{}] from source [{}]", pushEvent.getClass().getSimpleName(),
                pushEvent.getSource().getClass().getSimpleName());
        var pushEventHandler = getHandler(pushEvent);
        var pushRequest = pushEventHandler.handleEvent(pushEvent);
        //Validate push request before sent
        if (!isValid(pushRequest)) {
            log.warn("Skip sent invalid push event [{}]", pushEvent.getClass().getSimpleName());
        } else {
            sendWebPush(pushRequest);
        }
        log.info("Push event [{}] from source [{}] has been handled", pushEvent.getClass().getSimpleName(),
                pushEvent.getSource().getClass().getSimpleName());
    }

    private AbstractPushEventHandler getHandler(AbstractPushEvent pushEvent) {
        return pushEventHandlers.stream()
                .filter(handler -> handler.canHandle(pushEvent))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't handle push event [%s]", pushEvent.getClass().getSimpleName())));
    }

    @SuppressWarnings("unchecked")
    private boolean isValid(AbstractPushRequest pushRequest) {
        var validator = pushRequestValidators.stream()
                .filter(pushRequestValidator -> pushRequestValidator.canHandle(pushRequest))
                .findFirst()
                .orElse(null);
        //If validator isn't specified, returns true
        if (validator == null) {
            return true;
        }
        return validator.isValid(pushRequest);
    }

    private void sendWebPush(AbstractPushRequest pushRequest) {
        try {
            webPushSender.send(pushRequest);
        } catch (Exception ex) {
            log.error("Error while sent push request [{}], type [{}], message type [{}]: {}",
                    pushRequest.getRequestId(), pushRequest.getPushType(), pushRequest.getMessageType(),
                    ex.getMessage());
        }
    }
}
