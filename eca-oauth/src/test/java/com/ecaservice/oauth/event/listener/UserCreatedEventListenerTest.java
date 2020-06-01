package com.ecaservice.oauth.event.listener;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.event.model.UserCreatedEvent;
import com.ecaservice.oauth.service.mail.NotificationService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link UserCreatedEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class UserCreatedEventListenerTest {

    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private UserCreatedEventListener userCreatedEventListener;

    @Test
    void testHandleUserCreated() {
        userCreatedEventListener.handleUserCreatedEvent(
                new UserCreatedEvent(this, new UserEntity(), StringUtils.EMPTY));
        verify(notificationService, atLeastOnce()).notifyUserCreated(any(UserEntity.class), anyString());
    }
}
