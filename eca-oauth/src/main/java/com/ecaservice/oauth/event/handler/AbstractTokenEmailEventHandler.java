package com.ecaservice.oauth.event.handler;

import com.ecaservice.core.mail.client.event.listener.handler.AbstractEmailEventHandler;
import com.ecaservice.oauth.event.model.AbstractTokenEmailEvent;

/**
 * Abstract token email event handler.
 *
 * @param <T> - email event type
 * @author Roman Batygin
 */
public abstract class AbstractTokenEmailEventHandler<T extends AbstractTokenEmailEvent>
        extends AbstractEmailEventHandler<T> {

    protected AbstractTokenEmailEventHandler(Class<T> type) {
        super(type);
    }

    @Override
    public String getReceiver(AbstractTokenEmailEvent event) {
        return event.getTokenModel().getEmail();
    }
}
