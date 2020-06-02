package com.ecaservice.oauth.event.listener;

import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.event.model.ResetPasswordRequestCreatedEvent;
import com.ecaservice.oauth.service.mail.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link ResetPasswordRequestCreatedEventListener} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ResetPasswordRequestCreatedEventListenerTest {

    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private ResetPasswordRequestCreatedEventListener resetPasswordRequestCreatedEventListener;

    @Test
    void testHandleResetPasswordRequestCreated() {
        resetPasswordRequestCreatedEventListener.handleResetPasswordRequestCreatedEvent(
                new ResetPasswordRequestCreatedEvent(this, new ResetPasswordRequestEntity()));
        verify(notificationService, atLeastOnce()).sendResetPasswordLink(any(ResetPasswordRequestEntity.class));
    }
}
