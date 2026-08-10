package com.ecaservice.notification.service.handler;

import com.ecaservice.notification.event.model.UserPushEvent;
import com.ecaservice.notification.service.NotificationOptionsWebPushChecker;
import com.ecaservice.notification.service.UserNotificationService;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

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
    private final NotificationOptionsWebPushChecker notificationOptionsWebPushChecker;

    /**
     * Constructor with parameters.
     *
     * @param userNotificationService           - user notification service
     * @param applicationEventPublisher         - application event publisher
     * @param notificationOptionsWebPushChecker - notification options web push checker
     */
    public UserNotificationPushRequestHandler(UserNotificationService userNotificationService,
                                              ApplicationEventPublisher applicationEventPublisher,
                                              NotificationOptionsWebPushChecker notificationOptionsWebPushChecker) {
        super(UserPushNotificationRequest.class);
        this.userNotificationService = userNotificationService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.notificationOptionsWebPushChecker = notificationOptionsWebPushChecker;
    }

    @Override
    public void handle(UserPushNotificationRequest userPushNotificationRequest) {
        log.info("Received user push notification request [{}] type [{}] from initiator [{}] to receivers {}",
                userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getMessageType(),
                userPushNotificationRequest.getInitiator(), userPushNotificationRequest.getReceivers());
        List<String> finalReceivers = userPushNotificationRequest.getReceivers()
                .stream()
                .filter(user -> notificationOptionsWebPushChecker.isEnabled(user,
                        userPushNotificationRequest.getMessageType()))
                .toList();
        if (CollectionUtils.isEmpty(finalReceivers)) {
            log.info("No one receivers for  user push notification request [{}] type [{}] from initiator [{}]",
                    userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getMessageType(),
                    userPushNotificationRequest.getInitiator());
        } else {
            var finalRequest = createNewRequestFrom(userPushNotificationRequest, finalReceivers);
            userNotificationService.save(finalRequest);
            applicationEventPublisher.publishEvent(new UserPushEvent(this, finalRequest));
            log.info("User push notification request [{}] type [{}] has been processed",
                    userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getMessageType());
        }
    }

    private UserPushNotificationRequest createNewRequestFrom(UserPushNotificationRequest userPushNotificationRequest,
                                                             List<String> finalReceivers) {
        UserPushNotificationRequest finalRequest = new UserPushNotificationRequest();
        finalRequest.setRequestId(userPushNotificationRequest.getRequestId());
        finalRequest.setCreated(userPushNotificationRequest.getCreated());
        finalRequest.setInitiator(userPushNotificationRequest.getInitiator());
        finalRequest.setReceivers(finalReceivers);
        finalRequest.setCorrelationId(userPushNotificationRequest.getCorrelationId());
        finalRequest.setAdditionalProperties(userPushNotificationRequest.getAdditionalProperties());
        finalRequest.setMessageType(userPushNotificationRequest.getMessageType());
        finalRequest.setMessageText(userPushNotificationRequest.getMessageText());
        return finalRequest;
    }
}
