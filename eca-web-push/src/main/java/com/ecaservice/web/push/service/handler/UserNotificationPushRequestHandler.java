package com.ecaservice.web.push.service.handler;

import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.event.model.UserPushEvent;
import com.ecaservice.web.push.service.UserNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * User push notification request handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class UserNotificationPushRequestHandler extends AbstractPushRequestHandler<UserPushNotificationRequest> {

    private final UserNotificationService userNotificationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Constructor with parameters.
     *
     * @param userNotificationService   - user notification service
     * @param applicationEventPublisher - application event publisher
     */
    public UserNotificationPushRequestHandler(UserNotificationService userNotificationService,
                                              ApplicationEventPublisher applicationEventPublisher) {
        super(UserPushNotificationRequest.class);
        this.userNotificationService = userNotificationService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void handle(UserPushNotificationRequest userPushNotificationRequest) {
        log.info("Received user push notification request [{}] type [{}] from initiator [{}] to receivers {}",
                userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getMessageType(),
                userPushNotificationRequest.getInitiator(), userPushNotificationRequest.getReceivers());
        userNotificationService.save(userPushNotificationRequest);
        applicationEventPublisher.publishEvent(new UserPushEvent(this, userPushNotificationRequest));
        log.info("User push notification request [{}] type [{}] has been processed",
                userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getMessageType());
    }
}
