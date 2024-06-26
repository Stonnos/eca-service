package com.ecaservice.web.push.service.handler;

import com.ecaservice.web.push.config.ws.QueueConfig;
import com.ecaservice.web.push.dto.SystemPushRequest;
import com.ecaservice.web.push.mapping.NotificationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * System push request handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class SystemPushNotificationRequestHandler extends AbstractPushRequestHandler<SystemPushRequest> {

    private final QueueConfig queueConfig;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Constructor with parameters.
     *
     * @param queueConfig        - queue config
     * @param notificationMapper - notification mapper
     * @param messagingTemplate  - messaging template
     */
    public SystemPushNotificationRequestHandler(QueueConfig queueConfig,
                                                NotificationMapper notificationMapper,
                                                SimpMessagingTemplate messagingTemplate) {
        super(SystemPushRequest.class);
        this.queueConfig = queueConfig;
        this.notificationMapper = notificationMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void handle(SystemPushRequest systemPushRequest) {
        log.info("Received system push request [{}] message type [{}], correlation id [{}], additional properties {}",
                systemPushRequest.getRequestId(), systemPushRequest.getMessageType(),
                systemPushRequest.getCorrelationId(), systemPushRequest.getAdditionalProperties());
        String queue = queueConfig.getPushQueue();
        try {
            log.info("Starting to sent system push request [{}, [{}]], correlation id [{}] to queue [{}]",
                    systemPushRequest.getRequestId(), systemPushRequest.getMessageType(),
                    systemPushRequest.getCorrelationId(), queue);
            var pushRequestDto = notificationMapper.map(systemPushRequest);
            messagingTemplate.convertAndSend(queue, pushRequestDto);
            log.info("System push request [{}, [{}]], correlation id [{}] has been send to queue [{}]",
                    systemPushRequest.getRequestId(), systemPushRequest.getMessageType(),
                    systemPushRequest.getCorrelationId(), queue);
        } catch (Exception ex) {
            log.info("Error while sent system push request [{}, [{}]], correlation id [{}] to queue [{}]: {}",
                    systemPushRequest.getRequestId(), systemPushRequest.getMessageType(),
                    systemPushRequest.getCorrelationId(), queue, ex.getMessage());
        }
    }
}
