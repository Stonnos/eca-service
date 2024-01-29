package com.ecaservice.oauth.event.listener;

import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import com.ecaservice.oauth.event.model.UserProfileOptionsDataEvent;
import com.ecaservice.oauth.mapping.UserProfileOptionsMapperImpl;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.repository.UserNotificationEventOptionsRepository;
import com.ecaservice.oauth.repository.UserProfileOptionsRepository;
import com.ecaservice.oauth.service.UserProfileOptionsDataEventSender;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.Collections;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static com.ecaservice.oauth.TestHelperUtils.createUserProfileOptionsEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link UserProfileOptionsDataEventListener} functionality.
 *
 * @author Roman Batygin
 */
@Import({UserProfileOptionsMapperImpl.class, UserProfileOptionsDataEventListener.class})
class UserProfileOptionsDataEventListenerTest extends AbstractJpaTest {

    @MockBean
    private UserProfileOptionsDataEventSender userProfileOptionsDataEventSender;

    @Inject
    private UserEntityRepository userEntityRepository;
    @Inject
    private UserProfileOptionsRepository userProfileOptionsRepository;
    @Inject
    private UserNotificationEventOptionsRepository userNotificationEventOptionsRepository;

    @Inject
    private UserProfileOptionsDataEventListener userProfileOptionsDataEventListener;

    @Captor
    private ArgumentCaptor<UserProfileOptionsDto> userProfileOptionsDtoArgumentCaptor;

    private UserEntity userEntity;

    private UserProfileOptionsEntity userProfileOptionsEntity;

    @Override
    public void init() {
        userEntity = createAndSaveUser();
        userProfileOptionsEntity = createUserProfileOptionsEntity(userEntity);
        userProfileOptionsRepository.save(userProfileOptionsEntity);
        userNotificationEventOptionsRepository.saveAll(userProfileOptionsEntity.getNotificationEventOptions());
    }

    @Override
    public void deleteAll() {
        userNotificationEventOptionsRepository.deleteAll();
        userProfileOptionsRepository.deleteAll();
        userEntityRepository.deleteAll();
    }

    @Test
    void testSendUserProfileOptions() {
        userProfileOptionsDataEventListener.handleEvent(new UserProfileOptionsDataEvent(this, userEntity));
        verify(userProfileOptionsDataEventSender, atLeastOnce()).send(userProfileOptionsDtoArgumentCaptor.capture());
        var userProfileOptionsDto = userProfileOptionsDtoArgumentCaptor.getValue();
        assertThat(userProfileOptionsDto).isNotNull();
        assertThat(userProfileOptionsDto).isNotNull();
        assertThat(userProfileOptionsDto.isEmailEnabled()).isEqualTo(userProfileOptionsEntity.isEmailEnabled());
        assertThat(userProfileOptionsDto.isWebPushEnabled()).isEqualTo(userProfileOptionsEntity.isWebPushEnabled());
        assertThat(userProfileOptionsDto.getUser()).isEqualTo(userProfileOptionsEntity.getUserEntity().getLogin());
        assertThat(userProfileOptionsDto.getVersion()).isEqualTo(userProfileOptionsEntity.getVersion());
        assertThat(userProfileOptionsDto.getNotificationEventOptions()).hasSameSizeAs(
                userProfileOptionsEntity.getNotificationEventOptions());
    }

    private UserEntity createAndSaveUser() {
        UserEntity userEntity = createUserEntity();
        userEntity.setRoles(Collections.emptySet());
        return userEntityRepository.save(userEntity);
    }
}
