package com.ecaservice.server.service.push.validator;

import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * User notification push request validator.
 *
 * @author Roman Batygin
 */
@Component
public class UserNotificationPushRequestValidator extends PushRequestValidator<UserPushNotificationRequest> {

    public UserNotificationPushRequestValidator() {
        super(UserPushNotificationRequest.class);
    }

    @Override
    public boolean isValid(UserPushNotificationRequest pushRequest) {
        return !CollectionUtils.isEmpty(pushRequest.getReceivers());
    }
}
