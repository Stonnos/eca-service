package com.ecaservice.server.event.listener.push;

import com.ecaservice.server.event.model.push.AbstractUserPushNotificationEvent;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Abstract user push notification event handler.
 *
 * @param <E> - push request generic type
 * @author Roman Batygin
 */
public abstract class AbstractUserPushNotificationEventHandler<E extends AbstractUserPushNotificationEvent>
        extends AbstractPushEventHandler<E, UserPushNotificationRequest> {

    protected AbstractUserPushNotificationEventHandler(Class<E> clazz) {
        super(clazz);
    }

    @Override
    protected UserPushNotificationRequest internalCreatePushRequest(E event) {
        var userPushNotificationRequest = new UserPushNotificationRequest();
        userPushNotificationRequest.setInitiator(event.getInitiator());
        userPushNotificationRequest.setReceivers(getReceivers());
        userPushNotificationRequest.setCreated(LocalDateTime.now());
        return userPushNotificationRequest;
    }

    /**
     * Gets receivers list.
     *
     * @return receivers list
     */
    protected abstract List<String> getReceivers();
}
