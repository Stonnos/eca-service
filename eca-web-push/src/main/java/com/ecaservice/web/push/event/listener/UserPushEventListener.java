package com.ecaservice.web.push.event.listener;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.web.push.config.ws.QueueConfig;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.entity.PushTokenEntity;
import com.ecaservice.web.push.event.model.UserPushEvent;
import com.ecaservice.web.push.mapping.NotificationMapper;
import com.ecaservice.web.push.repository.PushTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
     * @param userPushEvent - user push event
     */
    @EventListener
    public void handlePushEvent(UserPushEvent userPushEvent) {
        var userPushNotificationRequest = userPushEvent.getUserPushNotificationRequest();
        log.info("Starting to handle user notification push event [{}]", userPushNotificationRequest.getRequestId());
        var pushTokens = getValidPushTokens(userPushNotificationRequest);
        log.info("[{}] valid push tokens has been found for push event [{}] with receivers [{}]", pushTokens.size(),
                userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getReceivers());
        pushTokens.forEach(pushTokenEntity -> sendPush(pushTokenEntity, userPushNotificationRequest));
        log.info("User notification push event [{}] has been processed", userPushNotificationRequest.getRequestId());
    }

    private List<PushTokenEntity> getValidPushTokens(UserPushNotificationRequest userPushNotificationRequest) {
        return pushTokenRepository.getNotExpiredTokens(userPushNotificationRequest.getReceivers(), LocalDateTime.now());
    }

    private void sendPush(PushTokenEntity pushTokenEntity,
                          UserPushNotificationRequest userPushNotificationRequest) {
        String tokenId = encryptorBase64AdapterService.decrypt(pushTokenEntity.getTokenId());
        String queue = String.format("%s/%s", queueConfig.getPushQueue(), tokenId);
        try {
            log.info("Starting to sent user push request [{}, [{}]] to ws queue for user [{}]",
                    userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getMessageType(),
                    pushTokenEntity.getUser());
            var pushRequestDto = notificationMapper.mapUserPushRequest(userPushNotificationRequest);
            messagingTemplate.convertAndSend(queue, pushRequestDto);
            log.info("User [{}] push request [{}, [{}]] has been send to ws queue", pushTokenEntity.getUser(),
                    userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getMessageType());
        } catch (Exception ex) {
            log.error("Error while sent user push request [{}, [{}]] to ws queue for user [{}]: {}",
                    userPushNotificationRequest.getRequestId(), userPushNotificationRequest.getMessageType(),
                    pushTokenEntity.getUser(), ex.getMessage());
        }
    }
}
