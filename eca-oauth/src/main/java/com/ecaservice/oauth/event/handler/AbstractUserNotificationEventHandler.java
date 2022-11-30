package com.ecaservice.oauth.event.handler;

import com.ecaservice.core.mail.client.event.listener.handler.AbstractEmailEventHandler;
import com.ecaservice.oauth.event.model.AbstractUserNotificationEvent;

/**
 * Abstract user notification event handler.
 *
 * @param <T> - email event type
 * @author Roman Batygin
 */
public abstract class AbstractUserNotificationEventHandler<T extends AbstractUserNotificationEvent>
        extends AbstractEmailEventHandler<T> {

    protected AbstractUserNotificationEventHandler(Class<T> type, String templateCode) {
        super(type, templateCode);
    }

    @Override
    public String getReceiver(AbstractUserNotificationEvent event) {
        return event.getUserEntity().getEmail();
    }
}
