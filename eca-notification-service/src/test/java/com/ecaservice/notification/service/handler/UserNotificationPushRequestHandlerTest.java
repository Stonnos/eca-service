package com.ecaservice.notification.service.handler;

import com.ecaservice.notification.service.NotificationOptionsWebPushChecker;
import com.ecaservice.notification.service.UserNotificationService;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.ecaservice.notification.TestHelperUtils.createUserPushNotificationRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserNotificationService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(UserNotificationPushRequestHandler.class)
class UserNotificationPushRequestHandlerTest {

    @MockBean
    private UserNotificationService userNotificationService;
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;
    @MockBean
    private NotificationOptionsWebPushChecker notificationOptionsWebPushChecker;

    @Captor
    private ArgumentCaptor<UserPushNotificationRequest> requestCapture;

    @Autowired
    private UserNotificationPushRequestHandler userNotificationPushRequestHandler;

    @Test
    void testHandleUserNotification() {
        when(notificationOptionsWebPushChecker.isEnabled(anyString(), anyString())).thenReturn(true);
        var userPushNotificationRequest = createUserPushNotificationRequest();
        userNotificationPushRequestHandler.handle(userPushNotificationRequest);
        verify(userNotificationService, atLeastOnce()).save(requestCapture.capture());
        UserPushNotificationRequest finalRequest = requestCapture.getValue();
        assertThat(finalRequest.getRequestId()).isEqualTo(userPushNotificationRequest.getRequestId());
        assertThat(finalRequest.getInitiator()).isEqualTo(userPushNotificationRequest.getInitiator());
        assertThat(finalRequest.getCreated()).isEqualTo(userPushNotificationRequest.getCreated());
        assertThat(finalRequest.getMessageType()).isEqualTo(userPushNotificationRequest.getMessageType());
        assertThat(finalRequest.getMessageText()).isEqualTo(userPushNotificationRequest.getMessageText());
        assertThat(finalRequest.getCorrelationId()).isEqualTo(userPushNotificationRequest.getCorrelationId());
        assertThat(finalRequest.getAdditionalProperties()).hasSameSizeAs(userPushNotificationRequest.getAdditionalProperties());
        assertThat(finalRequest.getReceivers()).hasSameSizeAs(userPushNotificationRequest.getReceivers());
    }
}
