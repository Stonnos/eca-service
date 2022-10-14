package com.ecaservice.web.push.service.handler;

import com.ecaservice.web.push.AbstractJpaTest;
import com.ecaservice.web.push.entity.MessageStatus;
import com.ecaservice.web.push.mapping.NotificationMapperImpl;
import com.ecaservice.web.push.repository.NotificationRepository;
import com.ecaservice.web.push.service.UserNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.web.push.TestHelperUtils.createUserPushNotificationRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link UserNotificationService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({UserNotificationPushRequestHandler.class, NotificationMapperImpl.class})
class UserNotificationPushRequestHandlerTest extends AbstractJpaTest {

    @Inject
    private NotificationRepository notificationRepository;

    @Inject
    private UserNotificationPushRequestHandler userNotificationPushRequestHandler;

    @Override
    public void deleteAll() {
        notificationRepository.deleteAll();
    }

    @Test
    void testSaveUserNotification() {
        var userPushNotificationRequest = createUserPushNotificationRequest();
        userNotificationPushRequestHandler.handle(userPushNotificationRequest);
        var notificationEntities = notificationRepository.findAll();
        assertThat(notificationEntities).hasSameSizeAs(userPushNotificationRequest.getReceivers());
        var notificationEntity = notificationEntities.iterator().next();
        assertThat(notificationEntity.getCreated()).isNotNull();
        assertThat(notificationEntity.getInitiator()).isEqualTo(userPushNotificationRequest.getInitiator());
        assertThat(notificationEntity.getMessageText()).isEqualTo(userPushNotificationRequest.getMessageText());
        assertThat(notificationEntity.getMessageType()).isEqualTo(userPushNotificationRequest.getMessageType());
        String expectedReceiver = userPushNotificationRequest.getReceivers().iterator().next();
        assertThat(notificationEntity.getReceiver()).isEqualTo(expectedReceiver);
        assertThat(notificationEntity.getMessageStatus()).isEqualTo(MessageStatus.NOT_READ);
        assertThat(notificationEntity.getParameters().size()).isEqualTo(
                userPushNotificationRequest.getAdditionalProperties().size());
    }
}
