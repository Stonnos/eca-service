package com.ecaservice.web.push.event.listener;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.web.push.config.ws.QueueConfig;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.entity.PushTokenEntity;
import com.ecaservice.web.push.mapping.NotificationMapper;
import com.ecaservice.web.push.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * User push event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPushEventListener {

    private final QueueConfig queueConfig;
    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final EncryptorBase64AdapterService encryptorBase64AdapterService;
    private final PushTokenRepository pushTokenRepository;

    /**
     * Handles user push event.
     *
     * @param userPushNotificationRequest - user push notification request
     */
    @EventListener
    public void handlePushEvent(UserPushNotificationRequest userPushNotificationRequest) {
        log.info("Starting to handle user notification push event [{}]", userPushNotificationRequest.getRequestId());
        var pushTokens = pushTokenRepository.findByUserIn(userPushNotificationRequest.getReceivers())
                .stream()
                .collect(Collectors.toMap(PushTokenEntity::getUser, Function.identity()));
        userPushNotificationRequest.getReceivers().forEach(user -> {
            var pushTokenEntity = pushTokens.get(user);
            if (pushTokenEntity == null) {
                log.warn("Push token not found for user [{}]. Skipped...", user);
            } else if (pushTokenEntity.isExpired()) {
                log.warn("Push token has been expired for user [{}]. Skipped...", user);
            } else {
                sendPush(pushTokenEntity, userPushNotificationRequest);
            }
        });
        log.info("Uer notification push event [{}] has been processed", userPushNotificationRequest.getRequestId());
    }

    private void sendPush(PushTokenEntity pushTokenEntity,
                          UserPushNotificationRequest userPushNotificationRequest) {
        String tokenId = encryptorBase64AdapterService.decrypt(pushTokenEntity.getTokenId());
        String queue = String.format("%s/%s", queueConfig.getPushQueue(), tokenId);
        log.info("Starting to sent user push request [{}, [{}]] to queue [{}]",
                userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getMessageType(), queue);
        var pushRequestDto = notificationMapper.mapUserPushRequest(userPushNotificationRequest);
        messagingTemplate.convertAndSend(queue, pushRequestDto);
        log.info("User push request [{}, [{}]] has been send to queue [{}]", userPushNotificationRequest.getRequestId(),
                userPushNotificationRequest.getMessageType(), queue);
    }
}
