package com.ecaservice.web.push.mapping;

import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.entity.MessageStatus;
import com.ecaservice.web.push.entity.NotificationEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.web.push.TestHelperUtils.createUserPushNotificationRequest;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link NotificationMapper} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(NotificationMapperImpl.class)
class NotificationMapperTest {

    @Inject
    private NotificationMapper notificationMapper;

    @Test
    void testMapUserNotificationRequest() {
        var userPushNotificationRequest = createUserPushNotificationRequest();
        var notificationEntity = notificationMapper.map(userPushNotificationRequest);
        assertThat(notificationEntity.getInitiator()).isEqualTo(userPushNotificationRequest.getInitiator());
        assertThat(notificationEntity.getMessageText()).isEqualTo(userPushNotificationRequest.getMessageText());
        assertThat(notificationEntity.getMessageType()).isEqualTo(userPushNotificationRequest.getMessageType());
        assertThat(notificationEntity.getParameters().size()).isEqualTo(
                userPushNotificationRequest.getAdditionalProperties().size());
        verifyParameters(userPushNotificationRequest, notificationEntity);
    }

    private void verifyParameters(UserPushNotificationRequest request, NotificationEntity notificationEntity) {
        notificationEntity.getParameters().forEach(notificationParameter -> {
            String value = request.getAdditionalProperties().get(notificationParameter.getName());
            assertThat(value).isNotNull();
            assertThat(notificationParameter.getValue()).isEqualTo(value);
        });
    }
}
