package com.ecaservice.oauth.event.handler;

import com.ecaservice.core.mail.client.event.listener.handler.AbstractEmailEventHandler;
import com.ecaservice.oauth.event.model.AbstractUserEmailEvent;

/**
 * Abstract user email event handler.
 *
 * @param <T> - email event type
 * @author Roman Batygin
 */
public abstract class AbstractUserEmailEventHandler<T extends AbstractUserEmailEvent>
        extends AbstractEmailEventHandler<T> {

    protected AbstractUserEmailEventHandler(Class<T> type) {
        super(type);
    }

    @Override
    public String getReceiver(AbstractUserEmailEvent event) {
        return event.getUserEntity().getEmail();
    }
}
