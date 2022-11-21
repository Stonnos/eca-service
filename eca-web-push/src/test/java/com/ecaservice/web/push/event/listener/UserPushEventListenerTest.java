package com.ecaservice.web.push.event.listener;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.web.dto.model.push.PushRequestDto;
import com.ecaservice.web.push.AbstractJpaTest;
import com.ecaservice.web.push.config.AppProperties;
import com.ecaservice.web.push.config.EncryptConfiguration;
import com.ecaservice.web.push.config.ws.QueueConfig;
import com.ecaservice.web.push.event.model.UserPushEvent;
import com.ecaservice.web.push.mapping.NotificationMapperImpl;
import com.ecaservice.web.push.repository.PushTokenRepository;
import com.ecaservice.web.push.service.PushTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.ecaservice.web.push.TestHelperUtils.createPushTokenEntity;
import static com.ecaservice.web.push.TestHelperUtils.createUserPushNotificationRequest;
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
        QueueConfig.class})
class UserPushEventListenerTest extends AbstractJpaTest {

    private static final String USER = "user";
    private static final String USER_2 = "user2";
    private static final String USER_3 = "user3";
    private static final String USER_4 = "user4";

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Inject
    private PushTokenRepository pushTokenRepository;
    @Inject
    private EncryptorBase64AdapterService encryptorBase64AdapterService;
    @Inject
    private AppProperties appProperties;

    @Inject
    private UserPushEventListener userPushEventListener;

    @Override
    public void deleteAll() {
        pushTokenRepository.deleteAll();
    }

    @Override
    public void init() {
        createAndSavePushToken(USER, LocalDateTime.now().plusMinutes(appProperties.getPushTokenValidityMinutes()));
        createAndSavePushToken(USER_2, LocalDateTime.now().plusMinutes(appProperties.getPushTokenValidityMinutes()));
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
