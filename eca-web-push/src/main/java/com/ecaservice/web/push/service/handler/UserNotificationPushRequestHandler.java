package com.ecaservice.web.push.service.handler;

import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.service.UserNotificationService;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * Constructor with parameters.
     *
     * @param userNotificationService - user notification nservice
     */
    public UserNotificationPushRequestHandler(UserNotificationService userNotificationService) {
        super(UserPushNotificationRequest.class);
        this.userNotificationService = userNotificationService;
    }

    @Override
    public void handle(UserPushNotificationRequest userPushNotificationRequest) {
        log.info("Received user push notification request [{}] type [{}] from initiator [{}] to receivers {}",
                userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getMessageType(),
                userPushNotificationRequest.getInitiator(), userPushNotificationRequest.getReceivers());
        userNotificationService.save(userPushNotificationRequest);
        log.info("User push notification request [{}] type [{}] has been processed",
                userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getMessageType());
    }
}
