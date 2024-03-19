package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static com.ecaservice.oauth.TestHelperUtils.createUserProfileOptionsEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link UserProfileOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(UserProfileOptionsMapperImpl.class)
class UserProfileMapperTest {

    @Inject
    private UserProfileOptionsMapper userProfileOptionsMapper;

    @Test
    void testMapToUserProfileOptionsDto() {
        UserEntity userEntity = createUserEntity();
        UserProfileOptionsEntity userProfileOptionsEntity = createUserProfileOptionsEntity(userEntity);
        var userProfileOptionsDto = userProfileOptionsMapper.mapToDto(userProfileOptionsEntity);
        assertThat(userProfileOptionsDto).isNotNull();
        assertThat(userProfileOptionsDto.getUser()).isEqualTo(userProfileOptionsEntity.getUserEntity().getLogin());
        assertThat(userProfileOptionsDto.getVersion()).isEqualTo(userProfileOptionsEntity.getVersion());
        assertThat(userProfileOptionsDto.isWebPushEnabled()).isEqualTo(userProfileOptionsEntity.isWebPushEnabled());
        assertThat(userProfileOptionsDto.isEmailEnabled()).isEqualTo(userProfileOptionsEntity.isEmailEnabled());
        assertThat(userProfileOptionsDto.getNotificationEventOptions()).hasSameSizeAs(
                userProfileOptionsEntity.getNotificationEventOptions());
        for (int i = 0; i < userProfileOptionsDto.getNotificationEventOptions().size(); i++) {
            var actual = userProfileOptionsDto.getNotificationEventOptions().get(i);
            var expected = userProfileOptionsEntity.getNotificationEventOptions().get(i);
            assertThat(actual.isEmailSupported()).isEqualTo(expected.isEmailSupported());
            assertThat(actual.isWebPushSupported()).isEqualTo(expected.isWebPushSupported());
            assertThat(actual.isEmailEnabled()).isEqualTo(expected.isEmailEnabled());
            assertThat(actual.isWebPushEnabled()).isEqualTo(expected.isWebPushEnabled());
            assertThat(actual.getEventType()).isEqualTo(expected.getEventType());
        }
    }

    @Test
    void testMapToUserProfileNotificationOptionsDto() {
        UserEntity userEntity = createUserEntity();
        UserProfileOptionsEntity userProfileOptionsEntity = createUserProfileOptionsEntity(userEntity);
        var userProfileNotificationOptionsDto =
                userProfileOptionsMapper.mapToNotificationOptionsDto(userProfileOptionsEntity);
        assertThat(userProfileNotificationOptionsDto).isNotNull();
        assertThat(userProfileNotificationOptionsDto.isWebPushEnabled()).isEqualTo(
                userProfileOptionsEntity.isWebPushEnabled());
        assertThat(userProfileNotificationOptionsDto.isEmailEnabled()).isEqualTo(
                userProfileOptionsEntity.isEmailEnabled());
        assertThat(userProfileNotificationOptionsDto.getNotificationEventOptions()).hasSameSizeAs(
                userProfileOptionsEntity.getNotificationEventOptions());
        for (int i = 0; i < userProfileNotificationOptionsDto.getNotificationEventOptions().size(); i++) {
            var actual = userProfileNotificationOptionsDto.getNotificationEventOptions().get(i);
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
