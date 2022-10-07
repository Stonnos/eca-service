package com.ecaservice.web.push.service;

import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.entity.MessageStatus;
import com.ecaservice.web.push.entity.NotificationEntity;
import com.ecaservice.web.push.mapping.NotificationMapper;
import com.ecaservice.web.push.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Notification service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;

    /**
     * Saves user push notification request.
     *
     * @param userPushNotificationRequest - user push notification request
     */
    public void save(UserPushNotificationRequest userPushNotificationRequest) {
        log.info("Starting to save user push notification request [{}] from initiator [{}] to receivers {}",
                userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getInitiator(),
                userPushNotificationRequest.getReceivers());
        var notifications = createNotifications(userPushNotificationRequest);
        notificationRepository.saveAll(notifications);
        log.info("[{}] notifications has been saved for push notification request [{}]",
                userPushNotificationRequest.getRequestId(), notifications.size());
    }

    private List<NotificationEntity> createNotifications(UserPushNotificationRequest userPushNotificationRequest) {
        return userPushNotificationRequest.getReceivers().stream()
                .map(receiver -> {
                    var notification = notificationMapper.map(userPushNotificationRequest);
                    notification.setReceiver(receiver);
                    notification.setMessageStatus(MessageStatus.NOT_READ);
                    notification.setCreated(LocalDateTime.now());
                    return notification;
                })
                .collect(Collectors.toList());
    }
}
