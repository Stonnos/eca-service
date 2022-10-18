package com.ecaservice.web.push.mapping;

import com.ecaservice.web.dto.model.push.PushType;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.entity.NotificationEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.web.push.TestHelperUtils.createNotificationEntity;
import static com.ecaservice.web.push.TestHelperUtils.createSystemPushRequest;
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
        assertThat(notificationEntity.getCreated()).isEqualTo(userPushNotificationRequest.getCreated());
        assertThat(notificationEntity.getParameters().size()).isEqualTo(
                userPushNotificationRequest.getAdditionalProperties().size());
        verifyParameters(userPushNotificationRequest, notificationEntity);
    }

    @Test
    void testMapToUserNotificationDto() {
        var notificationEntity = createNotificationEntity();
        var userNotificationDto = notificationMapper.map(notificationEntity);
        assertThat(userNotificationDto).isNotNull();
        assertThat(userNotificationDto.getCreated()).isEqualTo(notificationEntity.getCreated());
        assertThat(userNotificationDto.getInitiator()).isEqualTo(notificationEntity.getInitiator());
        assertThat(userNotificationDto.getMessageType()).isEqualTo(notificationEntity.getMessageType());
        assertThat(userNotificationDto.getMessageText()).isEqualTo(notificationEntity.getMessageText());
        assertThat(userNotificationDto.getMessageStatus().getValue()).isEqualTo(
                notificationEntity.getMessageStatus().name());
        assertThat(userNotificationDto.getMessageStatus().getDescription()).isEqualTo(
                notificationEntity.getMessageStatus().getDescription());
        assertThat(userNotificationDto.getParameters()).hasSameSizeAs(notificationEntity.getParameters());
    }

    @Test
    void testUserPushNotificationRequestToPushRequestDto() {
        var userPushNotificationRequest = createUserPushNotificationRequest();
        var pushRequestDto = notificationMapper.mapUserPushRequest(userPushNotificationRequest);
        assertThat(pushRequestDto.getInitiator()).isEqualTo(userPushNotificationRequest.getInitiator());
        assertThat(pushRequestDto.getMessageText()).isEqualTo(userPushNotificationRequest.getMessageText());
        assertThat(pushRequestDto.getMessageType()).isEqualTo(userPushNotificationRequest.getMessageType());
        assertThat(pushRequestDto.isShowMessage()).isTrue();
        assertThat(pushRequestDto.getRequestId()).isEqualTo(userPushNotificationRequest.getRequestId());
        assertThat(pushRequestDto.getPushType()).isEqualTo(PushType.USER_NOTIFICATION);
        assertThat(pushRequestDto.getAdditionalProperties()).containsAllEntriesOf(
                userPushNotificationRequest.getAdditionalProperties());
    }

    @Test
    void testSystemNotificationRequestToPushRequestDto() {
        var systemPushRequest = createSystemPushRequest();
        var pushRequestDto = notificationMapper.map(systemPushRequest);
        assertThat(pushRequestDto.getInitiator()).isNull();
        assertThat(pushRequestDto.getMessageText()).isEqualTo(systemPushRequest.getMessageText());
        assertThat(pushRequestDto.getMessageType()).isEqualTo(systemPushRequest.getMessageType());
        assertThat(pushRequestDto.isShowMessage()).isEqualTo(systemPushRequest.isShowMessage());
        assertThat(pushRequestDto.getRequestId()).isEqualTo(systemPushRequest.getRequestId());
        assertThat(pushRequestDto.getPushType()).isEqualTo(PushType.SYSTEM);
        assertThat(pushRequestDto.getAdditionalProperties()).containsAllEntriesOf(
                systemPushRequest.getAdditionalProperties());
    }

    private void verifyParameters(UserPushNotificationRequest request, NotificationEntity notificationEntity) {
        notificationEntity.getParameters().forEach(notificationParameter -> {
            String value = request.getAdditionalProperties().get(notificationParameter.getName());
            assertThat(value).isNotNull();
            assertThat(notificationParameter.getValue()).isEqualTo(value);
        });
    }
}
