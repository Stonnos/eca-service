package com.ecaservice.oauth.event.handler;

import com.ecaservice.core.mail.client.event.listener.handler.AbstractEmailEventHandler;
import com.ecaservice.oauth.event.model.AbstractTokenNotificationEvent;

/**
 * Abstract token notification event handler.
 *
 * @param <T> - email event type
 * @author Roman Batygin
 */
public abstract class AbstractTokenNotificationEventHandler<T extends AbstractTokenNotificationEvent>
        extends AbstractEmailEventHandler<T> {

    protected AbstractTokenNotificationEventHandler(Class<T> type, String templateCode) {
        super(type, templateCode);
    }

    @Override
    public String getReceiver(AbstractTokenNotificationEvent event) {
        return event.getTokenModel().getEmail();
    }
}
