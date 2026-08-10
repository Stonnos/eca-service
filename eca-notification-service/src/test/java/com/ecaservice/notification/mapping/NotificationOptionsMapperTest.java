package com.ecaservice.notification.mapping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.ecaservice.notification.TestHelperUtils.createUserProfileOptionsEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link NotificationOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(NotificationOptionsMapperImpl.class)
class NotificationOptionsMapperTest {

    private static final String USER = "admin";

    @Autowired
    private NotificationOptionsMapper notificationOptionsMapper;

    @Test
    void testMapToNotificationOptionsDto() {
        var userProfileOptionsEntity = createUserProfileOptionsEntity(USER);
        var notificationOptionsDto = notificationOptionsMapper.mapToNotificationOptionsDto(userProfileOptionsEntity);
        assertThat(notificationOptionsDto).isNotNull();
        assertThat(notificationOptionsDto.isWebPushEnabled()).isEqualTo(
                userProfileOptionsEntity.isWebPushEnabled());
        assertThat(notificationOptionsDto.isEmailEnabled()).isEqualTo(
                userProfileOptionsEntity.isEmailEnabled());
        assertThat(notificationOptionsDto.getNotificationEventOptions()).hasSameSizeAs(
                userProfileOptionsEntity.getNotificationEventOptions());
        for (int i = 0; i < notificationOptionsDto.getNotificationEventOptions().size(); i++) {
            var actual = notificationOptionsDto.getNotificationEventOptions().get(i);
            var expected = userProfileOptionsEntity.getNotificationEventOptions().get(i);
            assertThat(actual.isEmailSupported()).isEqualTo(expected.isEmailSupported());
            assertThat(actual.isWebPushSupported()).isEqualTo(expected.isWebPushSupported());
            assertThat(actual.isEmailEnabled()).isEqualTo(expected.isEmailEnabled());
            assertThat(actual.isWebPushEnabled()).isEqualTo(expected.isWebPushEnabled());
            assertThat(actual.getEventType()).isEqualTo(expected.getEventType().name());
            assertThat(actual.getEventDescription()).isEqualTo(expected.getEventType().getDescription());
        }
    }
}
