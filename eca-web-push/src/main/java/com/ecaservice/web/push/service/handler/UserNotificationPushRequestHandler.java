package com.ecaservice.web.push.service.handler;

import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.entity.MessageStatus;
import com.ecaservice.web.push.entity.NotificationEntity;
import com.ecaservice.web.push.mapping.NotificationMapper;
import com.ecaservice.web.push.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User push notification request handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class UserNotificationPushRequestHandler extends AbstractPushRequestHandler<UserPushNotificationRequest> {

    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;

    /**
     * Constructor with parameters.
     *
     * @param notificationMapper     - notification mapper
     * @param notificationRepository - notification repository
     */
    public UserNotificationPushRequestHandler(NotificationMapper notificationMapper,
                                              NotificationRepository notificationRepository) {
        super(UserPushNotificationRequest.class);
        this.notificationMapper = notificationMapper;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void handle(UserPushNotificationRequest userPushNotificationRequest) {
        log.info("Starting to save user push notification request [{}] type [{}] from initiator [{}] to receivers {}",
                userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getMessageType(),
                userPushNotificationRequest.getInitiator(), userPushNotificationRequest.getReceivers());
        var notifications = createNotifications(userPushNotificationRequest);
        notificationRepository.saveAll(notifications);
        log.info("[{}] notifications has been saved for push notification request [{}]",
                userPushNotificationRequest.getRequestId(), notifications.size());
    }

    private List<NotificationEntity> createNotifications(UserPushNotificationRequest userPushNotificationRequest) {
        return userPushNotificationRequest.getReceivers()
                .stream()
                .map(receiver -> {
                    var notification = notificationMapper.map(userPushNotificationRequest);
                    notification.setReceiver(receiver);
                    notification.setMessageStatus(MessageStatus.NOT_READ);
                    return notification;
                })
                .collect(Collectors.toList());
    }
}
