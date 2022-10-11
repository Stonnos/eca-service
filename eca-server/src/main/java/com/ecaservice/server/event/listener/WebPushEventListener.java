package com.ecaservice.server.event.listener;

import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.event.model.push.AbstractPushEvent;
import com.ecaservice.server.service.push.WebPushSender;
import com.ecaservice.server.service.push.handler.AbstractPushEventHandler;
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

    private final AppProperties appProperties;
    private final List<AbstractPushEventHandler> pushEventHandlers;
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
        if (!Boolean.TRUE.equals(appProperties.getNotifications().getWebPushesEnabled())) {
            log.warn("Web pushes are disabled. You may set [app.notifications.webPushesEnabled] property");
        } else {
            var pushEventHandler = getHandler(pushEvent);
            var pushRequest = pushEventHandler.handleEvent(pushEvent);
            webPushSender.send(pushRequest);
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
}
