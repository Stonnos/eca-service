package com.ecaservice.web.push.service.handler;

import com.ecaservice.web.push.service.UserNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.web.push.TestHelperUtils.createUserPushNotificationRequest;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

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

    @Inject
    private UserNotificationPushRequestHandler userNotificationPushRequestHandler;

    @Test
    void testHandleUserNotification() {
        var userPushNotificationRequest = createUserPushNotificationRequest();
        userNotificationPushRequestHandler.handle(userPushNotificationRequest);
        verify(userNotificationService, atLeastOnce()).save(userPushNotificationRequest);
    }
}
