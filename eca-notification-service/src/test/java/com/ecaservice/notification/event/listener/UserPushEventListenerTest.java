package com.ecaservice.notification.event.listener;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.notification.AbstractJpaTest;
import com.ecaservice.notification.config.AppProperties;
import com.ecaservice.notification.config.EncryptConfiguration;
import com.ecaservice.notification.config.WebPushProperties;
import com.ecaservice.notification.config.ws.QueueConfig;
import com.ecaservice.notification.event.model.UserPushEvent;
import com.ecaservice.notification.mapping.NotificationMapperImpl;
import com.ecaservice.notification.repository.PushTokenRepository;
import com.ecaservice.notification.service.PushTokenService;
import com.ecaservice.web.dto.model.push.PushRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.ecaservice.notification.TestHelperUtils.createPushTokenEntity;
import static com.ecaservice.notification.TestHelperUtils.createUserPushNotificationRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link PushTokenService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({EncryptConfiguration.class, AppProperties.class, UserPushEventListener.class, NotificationMapperImpl.class,
        QueueConfig.class, WebPushProperties.class})
class UserPushEventListenerTest extends AbstractJpaTest {

    private static final String USER = "user";
    private static final String USER_2 = "user2";
    private static final String USER_3 = "user3";
    private static final String USER_4 = "user4";

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PushTokenRepository pushTokenRepository;
    @Autowired
    private EncryptorBase64AdapterService encryptorBase64AdapterService;
    @Autowired
    private WebPushProperties webPushProperties;

    @Autowired
    private UserPushEventListener userPushEventListener;

    @Override
    public void deleteAll() {
        pushTokenRepository.deleteAll();
    }

    @Override
    public void init() {
        createAndSavePushToken(USER, LocalDateTime.now().plusMinutes(webPushProperties.getPushTokenValidityMinutes()));
        createAndSavePushToken(USER_2,
                LocalDateTime.now().plusMinutes(webPushProperties.getPushTokenValidityMinutes()));
        createAndSavePushToken(USER_3, LocalDateTime.now().minusMinutes(1L));
    }

    @Test
    void testHandleUserPushEvent() {
        var userPushNotificationRequest = createUserPushNotificationRequest();
        userPushNotificationRequest.setReceivers(List.of(USER, USER_2, USER_3, USER_4));
        var pushEvent = new UserPushEvent(this, userPushNotificationRequest);
        userPushEventListener.handlePushEvent(pushEvent);
        verify(messagingTemplate, times(2)).convertAndSend(anyString(), any(PushRequestDto.class));
    }

    private void createAndSavePushToken(String user, LocalDateTime expireAt) {
        String tokenId = encryptorBase64AdapterService.encrypt(UUID.randomUUID().toString());
        var pushToken = createPushTokenEntity(user, tokenId, expireAt);
        pushTokenRepository.save(pushToken);
    }
}
