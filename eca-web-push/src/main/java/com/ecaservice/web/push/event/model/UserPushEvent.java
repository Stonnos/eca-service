package com.ecaservice.web.push.event.model;

import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * User push event.
 *
 * @author Roman Batygin
 */
public class UserPushEvent extends ApplicationEvent {

    @Getter
    private final UserPushNotificationRequest userPushNotificationRequest;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source                      the object on which the event initially occurred or with
     *                                    which the event is associated (never {@code null})
     * @param userPushNotificationRequest - user push notification request
     */
    public UserPushEvent(Object source,
                         UserPushNotificationRequest userPushNotificationRequest) {
        super(source);
        this.userPushNotificationRequest = userPushNotificationRequest;
    }
}
